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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import com.github.jknack.handlebars.io.AbstractTemplateSource;

import io.wcm.devops.conga.resource.Resource;

/**
 * Charset-aware TemplateSource for handlebars.
 */
@SuppressWarnings("java:S2160") // equals/hashCode is implemented in base class
class CharsetAwareTemplateSource extends AbstractTemplateSource {

  private final Resource file;
  private final String templateCharset;
  private final String location;

  CharsetAwareTemplateSource(Resource file, String charset, String location) {
    this.file = file;
    this.templateCharset = charset;
    this.location = location;
  }

  @Override
  public String content(Charset charset) throws IOException {
    try (InputStream is = file.getInputStream()) {
      return IOUtils.toString(is, templateCharset);
    }
  }

  @Override
  public String filename() {
    return location;
  }

  @Override
  public long lastModified() {
    return file.getLastModified();
  }

}
