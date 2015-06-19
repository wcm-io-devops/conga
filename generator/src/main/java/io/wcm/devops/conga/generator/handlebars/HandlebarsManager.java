/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2015 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.devops.conga.generator.handlebars;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.resource.ResourceCollection;

import java.util.concurrent.ExecutionException;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Manages charset-aware handlebars instances.
 */
public class HandlebarsManager {

  private final ResourceCollection templateDir;

  private final LoadingCache<String, Handlebars> handlebarsCache =
      CacheBuilder.newBuilder().build(new CacheLoader<String, Handlebars>() {
        @Override
        public Handlebars load(String charset) throws Exception {
          TemplateLoader templateLoader = new CharsetAwareTemplateLoader(templateDir, charset);
          return new Handlebars(templateLoader);
        }
      });

  /**
   * @param templateDir Template base directory
   */
  public HandlebarsManager(ResourceCollection templateDir) {
    this.templateDir = templateDir;
  }

  /**
   * Get handelbars instance for charset.
   * @param charset Charset
   * @return Handlebars instance
   */
  public Handlebars get(String charset) {
    try {
      return handlebarsCache.get(charset);
    }
    catch (ExecutionException ex) {
      throw new GeneratorException("Unable to get handlebars instance for charset " + charset, ex);
    }
  }

}
