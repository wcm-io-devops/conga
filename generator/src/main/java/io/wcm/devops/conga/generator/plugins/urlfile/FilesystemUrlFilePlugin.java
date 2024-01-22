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
package io.wcm.devops.conga.generator.plugins.urlfile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

import org.apache.commons.lang3.StringUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.UrlFilePlugin;
import io.wcm.devops.conga.generator.spi.context.UrlFilePluginContext;
import io.wcm.devops.conga.generator.util.FileUtil;

/**
 * Get file from local filesystem.
 */
public class FilesystemUrlFilePlugin implements UrlFilePlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "filesystem";

  private static final String PREFIX = "file:";
  private static final String PREFIX_NODE = "file-node:";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean accepts(String url, UrlFilePluginContext context) {
    return StringUtils.startsWith(url, PREFIX)
        || StringUtils.startsWith(url, PREFIX_NODE);
  }

  @Override
  public String getFileName(String url, UrlFilePluginContext context) {
    File file = getFileInternal(url, context);
    return file.getName();
  }

  @Override
  public InputStream getFile(String url, UrlFilePluginContext context) throws IOException {
    File file = getFileInternal(url, context);
    if (!file.exists()) {
      throw new FileNotFoundException("File does not exist: " + FileUtil.getCanonicalPath(file));
    }
    return new BufferedInputStream(new FileInputStream(file));
  }

  @Override
  public URL getFileUrl(String url, UrlFilePluginContext context) throws IOException {
    File file = getFileInternal(url, context);
    if (!file.exists()) {
      throw new FileNotFoundException("File does not exist: " + FileUtil.getCanonicalPath(file));
    }
    return file.toURI().toURL();
  }

  @Override
  @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
  public void deleteFile(String url, UrlFilePluginContext context) throws IOException {
    File file = getFileInternal(url, context);
    if (!file.exists()) {
      throw new FileNotFoundException("File does not exist: " + FileUtil.getCanonicalPath(file));
    }
    Files.delete(file.toPath());
  }

  private static File getFileInternal(String url, UrlFilePluginContext context) {
    if (StringUtils.startsWith(url, PREFIX)) {
      String absolutePath = StringUtils.substringAfter(url, PREFIX);
      return new File(absolutePath);
    }
    else if (StringUtils.startsWith(url, PREFIX_NODE)) {
      File nodeBaseDir = context.getNodeBaseDir();
      if (nodeBaseDir != null) {
        String relativePath = StringUtils.substringAfter(url, PREFIX_NODE);
        return new File(nodeBaseDir, relativePath);
      }
      else {
        throw new GeneratorException("Unable to get node basedir outside not generation context.");
      }
    }
    else {
      String relativePath = url;
      return new File(context.getBaseDir(), relativePath);
    }
  }

}
