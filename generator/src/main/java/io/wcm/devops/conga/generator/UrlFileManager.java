/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2016 wcm.io
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
package io.wcm.devops.conga.generator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import io.wcm.devops.conga.generator.plugins.urlfile.FilesystemUrlFilePlugin;
import io.wcm.devops.conga.generator.spi.UrlFilePlugin;
import io.wcm.devops.conga.generator.spi.context.UrlFilePluginContext;
import io.wcm.devops.conga.generator.util.PluginManager;

/**
 * Manages copy/download of external files referenced in CONGA roles by URL, filesystem, classpath or Maven coordinates.
 */
public final class UrlFileManager {

  private final List<UrlFilePlugin> urlFilePlugins;
  private final UrlFilePlugin defaultUrlFilePlugin;
  private final UrlFilePluginContext context;

  private static final Pattern URL_WITH_PREFIX = Pattern.compile("^[a-zA-Z]+:.*$");

  /**
   * @param pluginManager Plugin manager
   * @param context URL file plugin context
   */
  public UrlFileManager(PluginManager pluginManager, UrlFilePluginContext context) {
    this.urlFilePlugins = pluginManager.getAll(UrlFilePlugin.class);
    this.defaultUrlFilePlugin = pluginManager.get(FilesystemUrlFilePlugin.NAME, UrlFilePlugin.class);
    this.context = context;
    this.context.getPluginContextOptions().urlFileManager(this);
  }

  /**
   * Get file name from URL.
   * @param url URL string
   * @return File name
   * @throws IOException I/O exception
   */
  public String getFileName(String url) throws IOException {
    if (StringUtils.isBlank(url)) {
      throw new IllegalArgumentException("No URL given.");
    }

    for (UrlFilePlugin plugin : urlFilePlugins) {
      if (plugin.accepts(url, context)) {
        return plugin.getFileName(url, context);
      }
    }

    // if path does not contain any prefix try to resolve relative path from filesystem
    if (!URL_WITH_PREFIX.matcher(url).matches()) {
      return defaultUrlFilePlugin.getFileName(url, context);
    }

    throw new IOException("No file URL plugin exists that supports the URL: " + url);
  }

  /**
   * Get file binary data from URL.
   * @param url URL string
   * @return Input stream
   * @throws IOException I/O exception
   */
  public InputStream getFile(String url) throws IOException {
    return handleFile(url, plugin -> plugin.getFile(url, context));
  }

  /**
   * Get URL to to binary file.
   * @param url URL string
   * @return URL
   * @throws IOException I/O exception
   */
  public URL getFileUrl(String url) throws IOException {
    return handleFile(url, plugin -> plugin.getFileUrl(url, context));
  }

  /**
   * Get version information from given file URL.
   * @param url URL string
   * @return Version or null if no version can be detected
   * @throws IOException I/O exception
   */
  public String getFileVersion(String url) throws IOException {
    return handleFile(url, plugin -> plugin.getFileVersion(url, context));
  }

  private <T> T handleFile(String url, FileHandler<T> fileHandler) throws IOException {
    if (StringUtils.isBlank(url)) {
      throw new IllegalArgumentException("No URL given.");
    }

    for (UrlFilePlugin plugin : urlFilePlugins) {
      if (plugin.accepts(url, context)) {
        return fileHandler.apply(plugin);
      }
    }

    // if path does not contain any prefix try to resolve relative path from filesystem
    if (!URL_WITH_PREFIX.matcher(url).matches()) {
      return fileHandler.apply(defaultUrlFilePlugin);
    }

    throw new IOException("No file URL plugin exists that supports the URL: " + url);
  }

  private interface FileHandler<T> {
    T apply(UrlFilePlugin plugin) throws IOException;
  }

}
