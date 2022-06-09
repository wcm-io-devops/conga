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
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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
      Number number = NumberUtils.createNumber(valueString);
      // edge case: a version number like 1.20 which looks like a double should be treated as string
      // so convert number back to string, and if it's different keep it as a string
      if (!StringUtils.equals(number.toString(), valueString)) {
        return valueString;
      }
      return number;
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

  /**
   * Convert a value to it's string representation.
   * @param value Value
   * @return String representation
   */
  @SuppressWarnings("unchecked")
  public static String valueToString(Object value) {
    if (value == null) {
      return "";
    }
    else if (value instanceof List) {
      StringBuilder sb = new StringBuilder();
      for (Object item : ((List)value)) {
        if (sb.length() > 0) {
          sb.append(",");
        }
        sb.append(valueToString(item));
      }
      return sb.toString();
    }
    else if (value instanceof Map) {
      StringBuilder sb = new StringBuilder();
      // use sorted map to ensure consistent order of keys
      SortedMap<Object, Object> sortedMap = new TreeMap<>((Map<Object, Object>)value);
      for (Map.Entry<Object, Object> entry : sortedMap.entrySet()) {
        if (sb.length() > 0) {
          sb.append(",");
        }
        sb.append(valueToString(entry.getKey()));
        sb.append("=");
        sb.append(valueToString(entry.getValue()));
      }
      return sb.toString();
    }
    else {
      return value.toString();
    }
  }

}
