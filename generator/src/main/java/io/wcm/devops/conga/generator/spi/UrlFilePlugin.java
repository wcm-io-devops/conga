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
package io.wcm.devops.conga.generator.spi;

import java.io.IOException;
import java.io.InputStream;

import io.wcm.devops.conga.generator.spi.context.UrlFilePluginContext;

/**
 * Get files from external sources.
 */
public interface UrlFilePlugin extends Plugin {

  /**
   * Checks if the plugin can be applied to the given URL.
   * @param url URL (including prefix)
   * @param context Context objects
   * @return true when the plugin can be applied to the given URL.
   */
  boolean accepts(String url, UrlFilePluginContext context);

  /**
   * Get filename for external file.
   * @param url URL (including prefix)
   * @param context Context objects
   * @return Filename
   */
  String getFileName(String url, UrlFilePluginContext context) throws IOException;

  /**
   * Get binary data of external file.
   * @param url URL (including prefix)
   * @param context Context objects
   * @return Binary data
   * @throws IOException If the access to the file failed
   */
  InputStream getFile(String url, UrlFilePluginContext context) throws IOException;

}
