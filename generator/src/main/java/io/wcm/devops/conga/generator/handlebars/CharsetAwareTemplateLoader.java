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

import io.wcm.devops.conga.generator.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.github.jknack.handlebars.io.AbstractTemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;

/**
 * Charset-aware template-loader for handlebars.
 */
public class CharsetAwareTemplateLoader extends AbstractTemplateLoader {

  private final File templateDir;
  private final String charset;

  /**
   * @param templateDir Template base directory
   * @param charset Charset for reading template files
   */
  public CharsetAwareTemplateLoader(File templateDir, String charset) {
    this.templateDir = templateDir;
    this.charset = charset;
  }

  @Override
  public TemplateSource sourceAt(String location) throws IOException {
    File file = new File(templateDir, location);
    if (!file.exists() || !file.isFile()) {
      throw new FileNotFoundException("File not found: " + FileUtil.getCanonicalPath(file));
    }
    return new CharsetAwareTemplateSource(file, charset, location);
  }

}
