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
package io.wcm.devops.conga.model.resolver;

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
   * @param config Config map with values with variable placeholders
   * @return Config map with values with variable placeholders resolved
   * @throws IllegalArgumentException when a variable name could not be resolve.d
   */
  public static Map<String, Object> resolve(Map<String, Object> config) {
    return resolve(config, 0);
  }

  private static Map<String, Object> resolve(Map<String, Object> config, int iterationCount) {
    if (iterationCount >= REPLACEMENT_MAX_ITERATIONS) {
      throw new IllegalArgumentException("Cyclic dependencies in config map detected: " + config);
    }

    Map<String, Object> mapCopy = new HashMap<>(config);

    boolean replacedAny = false;
    for (Map.Entry<String, Object> entry : mapCopy.entrySet()) {
      Object replacedValue = replaceAny(entry.getValue(), mapCopy);
      if (entry.getValue() != replacedValue) {
        entry.setValue(replacedValue);
        replacedAny = true;
      }
    }

    if (replacedAny) {
      // try again until all nested references are resolved
      return resolve(mapCopy, iterationCount + 1);
    }
    else {
      return config;
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
    String replacedValue = VariableStringResolver.resolve(value, variables);
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

}
