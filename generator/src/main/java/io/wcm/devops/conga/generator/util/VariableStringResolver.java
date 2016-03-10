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

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import io.wcm.devops.conga.model.util.MapExpander;

/**
 * Resolve variables in a string referencing entries from a map.
 */
public final class VariableStringResolver {

  private static final Pattern VARIABLE_PATTERN = Pattern.compile("(\\\\?\\$)\\{([^\\}\\{\\$]+)\\}");
  private static final int REPLACEMENT_MAX_ITERATIONS = 20;

  private VariableStringResolver() {
    // static methods only
  }

  /**
   * Replace variable placeholders in a string with syntax ${key} with values from a map.
   * The variables can recursively reference each other.
   * All escaped variables are deescaped.
   * @param value Value with variable placeholders
   * @param variables Variable map
   * @return Value with variable placeholders resolved.
   * @throws IllegalArgumentException when a variable name could not be resolve.d
   */
  public static String resolve(String value, Map<String, Object> variables) {
    return resolve(value, variables, true);
  }

  /**
   * Replace variable placeholders in a string with syntax ${key} with values from a map.
   * The variables can recursively reference each other.
   * @param value Value with variable placeholders
   * @param variables Variable map
   * @param deescapeVariables If true, {@link #deescape(String)} is applied to the result string
   * @return Value with variable placeholders resolved.
   * @throws IllegalArgumentException when a variable name could not be resolve.d
   */
  public static String resolve(String value, Map<String, Object> variables, boolean deescapeVariables) {
    if (value == null) {
      return null;
    }

    String resolvedString = resolve(value, variables, 0);

    if (deescapeVariables) {
      resolvedString = deescape(resolvedString);
    }

    return resolvedString;
  }

  /**
   * De-escapes all escaped variables in the given string.
   * @param value String that may contain escaped variable references (starting with \$)
   * @return String with de-escaped variable references.
   */
  public static String deescape(String value) {
    return VARIABLE_PATTERN.matcher(value).replaceAll("\\$\\{$2\\}");
  }

  private static String resolve(String value, Map<String, Object> variables, int iterationCount) {
    if (iterationCount >= REPLACEMENT_MAX_ITERATIONS) {
      throw new IllegalArgumentException("Cyclic dependencies in variable string detected: " + value);
    }

    Matcher matcher = VARIABLE_PATTERN.matcher(value);
    StringBuffer sb = new StringBuffer();
    boolean replacedAny = false;
    while (matcher.find()) {
      boolean escapedVariable = StringUtils.equals(matcher.group(1), "\\$");
      String variable = matcher.group(2);
      if (escapedVariable) {
        // keep escaped variables intact
        matcher.appendReplacement(sb, Matcher.quoteReplacement("\\${" + variable + "}"));
      }
      else {
        Object valueObject = MapExpander.getDeep(variables, variable);
        if (valueObject != null) {
          String variableValue = valueToString(valueObject);
          matcher.appendReplacement(sb, Matcher.quoteReplacement(variableValue.toString()));
          replacedAny = true;
        }
        else {
          throw new IllegalArgumentException("Unknown variable: " + variable);
        }
      }
    }
    matcher.appendTail(sb);
    if (replacedAny) {
      // try again until all nested references are resolved
      return resolve(sb.toString(), variables, iterationCount + 1);
    }
    else {
      return sb.toString();
    }
  }

  @SuppressWarnings("unchecked")
  private static String valueToString(Object value) {
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
