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
package io.wcm.devops.conga.tooling.maven.plugin.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Helper methods for handling paths.
 */
public final class PathUtil {

  private PathUtil() {
    // static methods only
  }

  /**
   * Convert all slashes to "/".
   * @param path Path
   * @return Converted path
   */
  public static String unifySlashes(String path) {
    return StringUtils.replace(path, "\\", "/");
  }

}
