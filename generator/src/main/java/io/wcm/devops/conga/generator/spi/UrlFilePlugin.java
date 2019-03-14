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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import com.google.common.collect.ImmutableList;

import io.wcm.devops.conga.generator.spi.context.UrlFilePluginContext;

/**
 * Get files from external sources.
 */
public interface UrlFilePlugin extends Plugin {

  /**
   * Checks if the plugin can be applied to the given URL.
   * @param url URL string (including prefix)
   * @param context Context objects
   * @return true when the plugin can be applied to the given URL.
   */
  boolean accepts(String url, UrlFilePluginContext context);

  /**
   * Get filename for external file.
   * @param url URL string (including prefix)
   * @param context Context objects
   * @return Filename
   * @throws IOException I/O exception
   */
  String getFileName(String url, UrlFilePluginContext context) throws IOException;

  /**
   * Get binary data of external file.
   * @param url URL string (including prefix)
   * @param context Context objects
   * @return Binary data
   * @throws IOException If the access to the file failed
   */
  InputStream getFile(String url, UrlFilePluginContext context) throws IOException;

  /**
   * Get URL to external file.
   * @param url URL string (including prefix)
   * @param context Context objects
   * @return URL to file
   * @throws IOException If the access to the file failed
   */
  default URL getFileUrl(String url, UrlFilePluginContext context) throws IOException {
    throw new IOException("File URLs not supported for " + getClass().getName());
  }

  /**
   * Get URLs of transitive dependencies of external file. This usually applies only to Maven artifacts.
   * The returned list includes the URL of the artifact itself, and all it's transitive dependencies.
   * @param url URL string (including prefix)
   * @param context Context objects
   * @return URLs to files
   * @throws IOException If the access to the file failed
   */
  default List<URL> getFileUrlsWithDependencies(String url, UrlFilePluginContext context) throws IOException {
    return ImmutableList.of(getFileUrl(url, context));
  }

  /**
   * Delete file with given URL. This is only supported by few implementations of this interface - all other throw an
   * {@link UnsupportedOperationException}.
   * @param url URL string (including prefix)
   * @param context Context objects
   * @throws IOException If deleting the file is supported but failes for some reason
   */
  default void deleteFile(String url, UrlFilePluginContext context) throws IOException {
    throw new UnsupportedOperationException("Unable to delete file: " + url);
  }

  /**
   * Checks if the given file is a file from the local file system and can be safely referenced via symlink.
   * @param url URL string (including prefix)
   * @param context Context objects
   * @return true if file is a local file
   * @throws IOException If the access to the file failed
   */
  default boolean isLocalFile(String url, UrlFilePluginContext context) throws IOException {
    return false;
  }

  /**
   * Get local file reference to given URL.
   * @param url URL string (including prefix)
   * @param context Context objects
   * @return Local file reference.
   * @throws IOException If the access to the file failed
   */
  default File getLocalFile(String url, UrlFilePluginContext context) throws IOException {
    throw new IOException("Local file not supported for " + getClass().getName());
  }

}
