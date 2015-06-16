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

import io.wcm.devops.conga.model.shared.Configurable;
import io.wcm.devops.conga.model.util.MapMerger;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Iterates over all {@link Configurable} items in the object tree.
 * Configuration from parent objects is inherited to client objects and variables are resolved.
 */
public final class ConfigResolver {

  private ConfigResolver() {
    // static methods only
  }

  /**
   * Resolve all variables.
   * @param model Model with variables at any nested level.
   */
  public static void resolve(Object model) {
    resolve(model, new HashMap<>(), new HashMap<>());
  }

  private static void resolve(Object object, Map<String, Object> parentConfig, Map<String, Object> parentVariables) {
    if (object instanceof Map) {
      for (Object child : ((Map)object).values()) {
        resolve(child, parentConfig, parentVariables);
      }
      return;
    }
    if (object instanceof List) {
      for (Object child : (List)object) {
        resolve(child, parentConfig, parentVariables);
      }
      return;
    }
    Map<String, Object> config;
    if (object instanceof Configurable) {
      Configurable configurable = (Configurable)object;
      config = resolveConfigurable(configurable, parentConfig);
    }
    else {
      config = parentConfig;
    }
    resolveNestedObjects(object, config);
  }

  private static void resolveNestedObjects(Object model, Map<String, Object> parentConfig) {
    try {
      Map<String, String> description = BeanUtils.describe(model);
      for (String propertyName : description.keySet()) {
        Object propertyValue = PropertyUtils.getProperty(model, propertyName);
        if (!StringUtils.equals(propertyName, "class")) {
          resolve(propertyValue, parentConfig, parentConfig);
        }
      }
    }
    catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
      throw new RuntimeException("Unable to get bean properties from '" + model.getClass().getName() + "'.", ex);
    }
  }

  private static Map<String, Object> resolveConfigurable(Configurable configurable, Map<String, Object> parentConfig) {
    Map<String, Object> mergedConfig = MapMerger.merge(configurable.getConfig(), parentConfig);

    Map<String, Object> resolvedConfig = resolveConfig(mergedConfig, mergedConfig);

    configurable.resolved(resolvedConfig);

    return mergedConfig;
  }

  @SuppressWarnings("unchecked")
  private static Object resolveConfig(Object object, Map<String, Object> variables) {
    if (object instanceof Map) {
      return resolveConfig((Map<String, Object>)object, variables);
    }
    if (object instanceof List) {
      return resolveConfig((List<Object>)object, variables);
    }
    if (object instanceof String) {
      return VariableResolver.replaceVariables((String)object, variables);
    }
    return object;
  }

  private static Map<String, Object> resolveConfig(Map<String, Object> map, Map<String, Object> variables) {
    Map<String, Object> resolvedMap = new HashMap<>();
    for (Entry<String, Object> entry : map.entrySet()) {
      resolvedMap.put(entry.getKey(), resolveConfig(entry.getValue(), variables));
    }
    return resolvedMap;
  }

  private static List<Object> resolveConfig(List<Object> list, Map<String, Object> variables) {
    List<Object> resolvedList = new ArrayList<>();
    for (Object object : list) {
      resolvedList.add(resolveConfig(object, variables));
    }
    return resolvedList;
  }

}
