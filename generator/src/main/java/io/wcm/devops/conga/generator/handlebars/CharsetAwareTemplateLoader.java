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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.github.jknack.handlebars.io.AbstractTemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;

import io.wcm.devops.conga.resource.Resource;
import io.wcm.devops.conga.resource.ResourceCollection;

/**
 * Charset-aware template-loader for handlebars.
 */
public class CharsetAwareTemplateLoader extends AbstractTemplateLoader {

  private final List<ResourceCollection> templateDirs;
  private final String charset;

  /**
   * @param templateDirs Template base directories
   * @param charset Charset for reading template files
   */
  public CharsetAwareTemplateLoader(List<ResourceCollection> templateDirs, String charset) {
    this.templateDirs = templateDirs;
    this.charset = charset;
  }

  @Override
  public TemplateSource sourceAt(String location) throws IOException {
    Resource firstFile = null;
    for (ResourceCollection templateDir : templateDirs) {
      Resource file = templateDir.getResource(location);
      if (firstFile == null) {
        firstFile = file;
      }
      if (file.exists()) {
        return new CharsetAwareTemplateSource(file, charset, location);
      }
    }
    throw new FileNotFoundException("Template file not found: "
        + (firstFile != null ? firstFile.getCanonicalPath() : location));
  }

  @Override
  public String resolve(String uri) {
    Resource firstResource = null;
    for (ResourceCollection templateDir : templateDirs) {
      Resource file = templateDir.getResource(uri);
      if (firstResource == null) {
        firstResource = file;
      }
      if (file.exists()) {
        return file.getCanonicalPath();
      }
    }
    if (firstResource != null) {
      return firstResource.getCanonicalPath();
    }
    return null;
  }


}
