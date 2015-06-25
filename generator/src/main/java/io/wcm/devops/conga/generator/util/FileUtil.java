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
package io.wcm.devops.conga.generator.util;

import io.wcm.devops.conga.generator.spi.context.FileContext;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility methods for file handling.
 */
public final class FileUtil {

  private FileUtil() {
    // static methods only
  }

  /**
   * Get canoncial path of file
   * @param file File
   * @return Canonical path
   */
  public static String getCanonicalPath(File file) {
    try {
      return file.getCanonicalPath();
    }
    catch (IOException ex) {
      return file.getAbsolutePath();
    }
  }

  /**
   * Get canoncial path of file
   * @param fileContext File context
   * @return Canonical path
   */
  public static String getCanonicalPath(FileContext fileContext) {
    return getCanonicalPath(fileContext.getFile());
  }

  /**
   * Ensure that file exists.
   * @param file File
   * @return File
   */
  public static File ensureFileExists(File file) {
    if (!file.exists() || !file.isFile()) {
      throw new IllegalArgumentException("File not found: " + getCanonicalPath(file));
    }
    return file;
  }

  /**
   * Ensure that directory exists and create it if not.
   * @param dir Directory
   * @return Directory
   */
  public static File ensureDirExistsAutocreate(File dir) {
    if (!dir.exists()) {
      dir.mkdirs();
    }
    if (!dir.isDirectory()) {
      throw new IllegalArgumentException("Path is not a directory: " + getCanonicalPath(dir));
    }
    return dir;
  }

  /**
   * Checks file extension
   * @param fileExtension File extension of file to check
   * @param extension Expected file extension
   * @return true if file extension matches
   */
  public static boolean matchesExtension(String fileExtension, String extension) {
    return StringUtils.equalsIgnoreCase(fileExtension, extension);
  }

  /**
   * Checks file extension
   * @param file File to check
   * @param extension Expected file extension
   * @return true if file extension matches
   */
  public static boolean matchesExtension(File file, String extension) {
    return matchesExtension(FilenameUtils.getExtension(file.getName()), extension);
  }

  /**
   * Checks file extension
   * @param fileContext File context to check
   * @param extension Expected file extension
   * @return true if file extension matches
   */
  public static boolean matchesExtension(FileContext fileContext, String extension) {
    return matchesExtension(fileContext.getFile(), extension);
  }

}
