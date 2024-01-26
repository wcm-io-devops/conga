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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.model.environment.NodeRole;
import io.wcm.devops.conga.model.role.Role;
import io.wcm.devops.conga.model.role.RoleFile;

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
   * @deprecated use {@link FileContext#getCanonicalPath()} instead.
   */
  @Deprecated(forRemoval = true)
  public static String getCanonicalPath(FileContext fileContext) {
    return fileContext.getCanonicalPath();
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
  @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
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
    String fileName = file.getName();
    String fileExtension;
    // special handling for OSGi configuration resource file extension
    if (fileName.endsWith(".cfg.json")) {
      fileExtension = "cfg.json";
    }
    else {
      fileExtension = FilenameUtils.getExtension(fileName);
    }
    return matchesExtension(fileExtension, extension);
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

  /**
   * Get template path for a role file.
   * @param role Role
   * @param roleFile Role file
   * @return Path or null if not defined
   */
  public static String getTemplatePath(Role role, RoleFile roleFile) {
    String path = roleFile.getTemplate();
    if (StringUtils.isEmpty(path)) {
      return null;
    }
    if (StringUtils.isNotEmpty(role.getTemplateDir())) {
      path = FilenameUtils.concat(role.getTemplateDir(), path);
    }
    return path;
  }

  /**
   * Generates information to identify a file in a role definition by its file name or URL.
   * @param nodeRole Node role
   * @param roleFile Role file
   * @return Info string for role and file
   */
  public static String getFileInfo(NodeRole nodeRole, RoleFile roleFile) {
    return getFileInfo(nodeRole.getRole(), roleFile);
  }

  /**
   * Generates information to identify a file in a role definition by its file name or URL.
   * @param roleName Role name
   * @param roleFile Role file
   * @return Info string for role and file
   */
  public static String getFileInfo(String roleName, RoleFile roleFile) {
    StringBuilder sb = new StringBuilder().append(roleName).append("/");
    if (StringUtils.isNotEmpty(roleFile.getFile())) {
      sb.append(roleFile.getFile());
    }
    else if (StringUtils.isNotEmpty(roleFile.getUrl())) {
      sb.append(roleFile.getUrl());
    }
    else {
      sb.append("?");
    }
    return sb.toString();
  }

}
