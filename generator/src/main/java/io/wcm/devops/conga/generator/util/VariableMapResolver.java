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

import static io.wcm.devops.conga.generator.util.VariableStringResolver.SINGLE_EXPRESSION_PATTERN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;

import io.wcm.devops.conga.generator.spi.context.ValueProviderGlobalContext;

/**
 * Resolve variables in a map referencing other entries from the same map.
 */
public final class VariableMapResolver {

  /**
   * Special map property that references a list value to iterate on. For each list item a new item is created instead
   * of the map. The list items can be references with {@link #ITEM_VARIABLE}.
   */
  static final String LIST_VARIABLE_ITERATE = "_iterate_";

  /**
   * Special variable name to reference item inside list generated by {@link #LIST_VARIABLE_ITERATE}.
   */
  static final String ITEM_VARIABLE = "_item_";

  /**
   * Special variable name to reference item index inside list generated by {@link #LIST_VARIABLE_ITERATE}
   * (starting with 0).
   */
  static final String ITEM_INDEX_VARIABLE = "_itemIndex_";

  private static final int REPLACEMENT_MAX_ITERATIONS = 20;

  private final VariableStringResolver variableStringResolver;

  /**
   * @param valueProviderGlobalContext Value provider global context
   */
  public VariableMapResolver(ValueProviderGlobalContext valueProviderGlobalContext) {
    this.variableStringResolver = new VariableStringResolver(valueProviderGlobalContext, this);
  }

  /**
   * Replace variable placeholders in values of a map with syntax ${key} with values from the map itself.
   * The variables can recursively reference each other.
   * All escaped variables are deescaped.
   * @param config Config map with values with variable placeholders
   * @return Config map with values with variable placeholders resolved
   * @throws IllegalArgumentException when a variable name could not be resolve.d
   */
  public Map<String, Object> resolve(Map<String, Object> config) {
    return resolve(config, true);
  }

  /**
   * Replace variable placeholders in values of a map with syntax ${key} with values from the map itself.
   * The variables can recursively reference each other.
   * @param config Config map with values with variable placeholders
   * @param deescapeVariables De-escape variables
   * @return Config map with values with variable placeholders resolved
   * @throws IllegalArgumentException when a variable name could not be resolved.
   */
  public Map<String, Object> resolve(Map<String, Object> config, boolean deescapeVariables) {
    return resolve(config, deescapeVariables, 0);
  }

  /**
   * De-escapes all escaped variables in all string values in the given map.
   * @param config Config map with values that my contain escaped variable references (starting with \$)
   * @return Map with de-escaped variable references.
   */
  public Map<String, Object> deescape(Map<String, Object> config) {
    return deescapeMap(config);
  }

  @SuppressWarnings("PMD.CompareObjectsWithEquals")
  private Map<String, Object> resolve(Map<String, Object> config, boolean deescapeVariables, int iterationCount) {
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
  private Object replaceAny(Object value, Map<String, Object> variables) {
    if (value instanceof String) {
      return replaceObject((String)value, variables);
    }
    else if (value instanceof List) {
      return replaceList((List<Object>)value, variables);
    }
    else if (value instanceof Map) {
      Map<String, Object> map = (Map<String, Object>)value;
      if (map.containsKey(LIST_VARIABLE_ITERATE)) {
        return replaceIterate(map, variables);
      }
      else {
        return replaceMap(map, variables);
      }
    }
    else {
      return value;
    }
  }

  private Object replaceObject(String value, Map<String, Object> variables) {
    Object replacedValue = variableStringResolver.resolve(value, variables, false);
    if (replacedValue != null && replacedValue.equals(value)) {
      return value;
    }
    else {
      return replacedValue;
    }
  }

  @SuppressWarnings("PMD.CompareObjectsWithEquals")
  private List<Object> replaceList(List<Object> list, Map<String, Object> variables) {
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

  private Map<String, Object> replaceMap(Map<String, Object> map, Map<String, Object> variables) {
    Map<String, Object> mapCopy = new HashMap<>(map);
    boolean replacedAny = mapCopy.remove(LIST_VARIABLE_ITERATE) != null;
    for (Map.Entry<String, Object> entry : mapCopy.entrySet()) {
      Object replacedValue = replaceAny(entry.getValue(), variables);
      if (!Objects.equals(entry.getValue(), replacedValue)) {
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
  private List<Object> replaceIterate(Map<String, Object> map, Map<String, Object> variables) {
    Object listObject = map.get(LIST_VARIABLE_ITERATE);
    if (listObject instanceof String) {
      Matcher matcher = SINGLE_EXPRESSION_PATTERN.matcher((String)listObject);
      if (matcher.matches()) {
        listObject = variableStringResolver.resolve((String)listObject, variables);
        if (listObject == null) {
          throw new IllegalArgumentException("Unable to resolve variable: " + matcher.group(0));
        }
      }
    }
    if (!(listObject instanceof List)) {
      // allow to iterate over single values as well
      listObject = List.of(listObject);
    }
    Map<String, Object> variablesClone = new LinkedHashMap<>(ObjectCloner.deepClone(variables));
    List<Object> result = new ArrayList<>();
    int count = 0;
    for (Object item : (List<Object>)listObject) {
      variablesClone.put(ITEM_VARIABLE, item);
      variablesClone.put(ITEM_INDEX_VARIABLE, count++);
      result.add(replaceMap(map, variablesClone));
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private Object deescapeAny(Object value) {
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

  private String deescapeString(String value) {
    return variableStringResolver.deescape(value);
  }

  @SuppressWarnings("PMD.CompareObjectsWithEquals")
  private List<Object> deescapeList(List<Object> list) {
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

  private Map<String, Object> deescapeMap(Map<String, Object> map) {
    Map<String, Object> mapCopy = new HashMap<>(map);
    for (Map.Entry<String, Object> entry : mapCopy.entrySet()) {
      Object deescapedValue = deescapeAny(entry.getValue());
      if (!Objects.equals(entry.getValue(), deescapedValue)) {
        entry.setValue(deescapedValue);
      }
    }
    return mapCopy;
  }

}
