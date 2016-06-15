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

import java.util.regex.Pattern;

import io.wcm.devops.conga.model.shared.LineEndings;

/**
 * Utility methods for handling line endings in files.
 */
public final class LineEndingConverter {

  /**
   * Unix line pattern
   */
  private static final Pattern PATTERN_UNIX = Pattern.compile(Pattern.quote(LineEndings.unix.getLineEnding()));

  /**
   * Windows line ending regex pattern
   */
  private static final Pattern PATTERN_WINDOWS = Pattern.compile(Pattern.quote(LineEndings.windows.getLineEnding()));

  /**
   * MacOS line ending pattern
   */
  private static final Pattern PATTERN_MACOS_LEGACY = Pattern.compile(Pattern.quote(LineEndings.macos.getLineEnding()));

  private LineEndingConverter() {
    // static constants and methods only
  }

  /**
   * Normalizes all line endings that may be Windows or MacOS-style and converts them to Unix-style line endings.
   * @param value String containing any line endings.
   * @return String containing only unix line endings.
   */
  public static String normalizeToUnix(String value) {
    if (value == null) {
      return null;
    }
    String partialConverted = PATTERN_WINDOWS.matcher(value).replaceAll(LineEndings.unix.getLineEnding());
    return PATTERN_MACOS_LEGACY.matcher(partialConverted).replaceAll(LineEndings.unix.getLineEnding());
  }

  /**
   * Converts all unix line endings to the given line ending style.
   * @param normalizedValue String with line endings normalized to Unix-style
   * @param lineEndings Line ending style.
   * @return String with the given line ending style.
   */
  public static String convertTo(String normalizedValue, LineEndings lineEndings) {
    if (normalizedValue == null) {
      return null;
    }
    if (lineEndings == null || lineEndings == LineEndings.unix) {
      return normalizedValue;
    }
    return PATTERN_UNIX.matcher(normalizedValue).replaceAll(lineEndings.getLineEnding());
  }

}
