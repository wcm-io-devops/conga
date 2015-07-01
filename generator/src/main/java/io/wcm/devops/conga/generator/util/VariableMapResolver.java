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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Resolve variables in a map referencing other entries from the same map.
 */
public final class VariableMapResolver {

  private static final int REPLACEMENT_MAX_ITERATIONS = 20;

  private VariableMapResolver() {
    // static methods only
  }

  /**
   * Replace variable placeholders in values of a map with syntax ${key} with values from the map itself.
   * The variables can recursively reference each other.
   * All escaped variables are deescaped.
   * @param config Config map with values with variable placeholders
   * @return Config map with values with variable placeholders resolved
   * @throws IllegalArgumentException when a variable name could not be resolve.d
   */
  public static Map<String, Object> resolve(Map<String, Object> config) {
    return resolve(config, true);
  }

  /**
   * Replace variable placeholders in values of a map with syntax ${key} with values from the map itself.
   * The variables can recursively reference each other.
   * @param config Config map with values with variable placeholders
   * @return Config map with values with variable placeholders resolved
   * @throws IllegalArgumentException when a variable name could not be resolve.d
   */
  public static Map<String, Object> resolve(Map<String, Object> config, boolean deescapeVariables) {
    return resolve(config, deescapeVariables, 0);
  }

  /**
   * De-escapes all escaped variables in all string values in the given map.
   * @param config Config map with values that my contain escaped variable references (starting with \$)
   * @return Map with de-escaped variable references.
   */
  public static Map<String, Object> deescape(Map<String, Object> config) {
    return deescapeMap(config);
  }

  private static Map<String, Object> resolve(Map<String, Object> config, boolean deescapeVariables, int iterationCount) {
    if (iterationCount >= REPLACEMENT_MAX_ITERATIONS) {
      throw new IllegalArgumentException("Cyclic dependencies in config map detected: " + config);
    }

    Map<String, Object> mapCopy = replaceMap(config, config);
    boolean replacedAny = mapCopy != config;

    if (replacedAny) {
      // try again until all nested references are resolved
      return resolve(mapCopy, deescapeVariables, iterationCount + 1);
    }
    else {
      if (deescapeVariables) {
        return deescapeMap(config);
      }
      else {
        return config;
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static Object replaceAny(Object value, Map<String, Object> variables) {
    if (value instanceof String) {
      return replaceString((String)value, variables);
    }
    else if (value instanceof List) {
      return replaceList((List<Object>)value, variables);
    }
    else if (value instanceof Map) {
      return replaceMap((Map<String, Object>)value, variables);
    }
    else {
      return value;
    }
  }

  private static String replaceString(String value, Map<String, Object> variables) {
    String replacedValue = VariableStringResolver.resolve(value, variables, false);
    if (StringUtils.equals(value, replacedValue)) {
      return value;
    }
    else {
      return replacedValue;
    }
  }

  private static List<Object> replaceList(List<Object> list, Map<String, Object> variables) {
    boolean replacedAny = false;
    List<Object> listCopy = new ArrayList<>(list);
    for (int i = 0; i < listCopy.size(); i++) {
      Object item = listCopy.get(i);
      Object replacedValue = replaceAny(item, variables);
      if (item != replacedValue) {
        listCopy.set(i, replacedValue);
        replacedAny = true;
      }
    }
    if (replacedAny) {
      return listCopy;
    }
    else {
      return list;
    }
  }

  private static Map<String, Object> replaceMap(Map<String, Object> map, Map<String, Object> variables) {
    boolean replacedAny = false;
    Map<String, Object> mapCopy = new HashMap<>(map);
    for (Map.Entry<String, Object> entry : mapCopy.entrySet()) {
      Object replacedValue = replaceAny(entry.getValue(), variables);
      if (entry.getValue() != replacedValue) {
        entry.setValue(replacedValue);
        replacedAny = true;
      }
    }

    if (replacedAny) {
      return mapCopy;
    }
    else {
      return map;
    }
  }

  @SuppressWarnings("unchecked")
  private static Object deescapeAny(Object value) {
    if (value instanceof String) {
      return deescapeString((String)value);
    }
    else if (value instanceof List) {
      return deescapeList((List<Object>)value);
    }
    else if (value instanceof Map) {
      return deescapeMap((Map<String, Object>)value);
    }
    else {
      return value;
    }
  }

  private static String deescapeString(String value) {
    return VariableStringResolver.deescape(value);
  }

  private static List<Object> deescapeList(List<Object> list) {
    List<Object> listCopy = new ArrayList<>(list);
    for (int i = 0; i < listCopy.size(); i++) {
      Object item = listCopy.get(i);
      Object deescapedValue = deescapeAny(item);
      if (item != deescapedValue) {
        listCopy.set(i, deescapedValue);
      }
    }
    return listCopy;
  }

  private static Map<String, Object> deescapeMap(Map<String, Object> map) {
    Map<String, Object> mapCopy = new HashMap<>(map);
    for (Map.Entry<String, Object> entry : mapCopy.entrySet()) {
      Object deescapedValue = deescapeAny(entry.getValue());
      if (entry.getValue() != deescapedValue) {
        entry.setValue(deescapedValue);
      }
    }
    return mapCopy;
  }

}
