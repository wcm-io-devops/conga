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

import java.io.File;
import java.io.IOException;

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
   * Ensure that directory exists.
   * @param dir Directory
   * @return Directory
   */
  public static File ensureDirExists(File dir) {
    if (!dir.exists() || !dir.isDirectory()) {
      throw new IllegalArgumentException("Directory not found: " + getCanonicalPath(dir));
    }
    return dir;
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

}
