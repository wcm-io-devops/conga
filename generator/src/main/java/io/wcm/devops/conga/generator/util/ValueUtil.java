/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 wcm.io
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Value utilities.
 */
public final class ValueUtil {

  private static final String LIST_SEPARATOR = ",";

  private ValueUtil() {
    // static methods only
  }

  /**
   * Parses a string value and converts it to a value object (or list).
   * Supports String, Number, Boolean, and lists of those separated by ",".
   * @param valueString Value string.
   * @return Parsed value
   */
  public static Object stringToValue(String valueString) {
    if (valueString == null) {
      return null;
    }
    else if (StringUtils.contains(valueString, LIST_SEPARATOR)) {
      return stringToValueList(valueString);
    }
    else if (StringUtils.equalsIgnoreCase(valueString, "true")) {
      return true;
    }
    else if (StringUtils.equalsIgnoreCase(valueString, "false")) {
      return false;
    }
    else if (NumberUtils.isCreatable(valueString)) {
      return NumberUtils.createNumber(valueString);
    }
    else {
      return valueString;
    }
  }

  private static Object stringToValueList(String valueStringList) {
    List<Object> list = new ArrayList<>();
    String[] valueStrings = StringUtils.splitPreserveAllTokens(valueStringList, LIST_SEPARATOR);
    for (String valueString : valueStrings) {
      list.add(stringToValue(valueString));
    }
    return list;
  }

}
