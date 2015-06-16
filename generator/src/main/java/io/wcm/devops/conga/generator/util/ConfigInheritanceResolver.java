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

import io.wcm.devops.conga.model.shared.Configurable;
import io.wcm.devops.conga.model.util.MapMerger;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Iterates over all {@link Configurable} items in the object tree.
 * Configuration from parent objects is inherited to client objects.
 * Variables are not resolved though, this is done during configuration generation process.
 */
public final class ConfigInheritanceResolver {

  private ConfigInheritanceResolver() {
    // static methods only
  }

  /**
   * Resolve all variables.
   * @param model Model with variables at any nested level.
   */
  public static void resolve(Object model) {
    resolve(model, new HashMap<>());
  }

  private static void resolve(Object object, Map<String, Object> parentConfig) {
    if (object instanceof Map) {
      for (Object child : ((Map)object).values()) {
        resolve(child, parentConfig);
      }
      return;
    }
    if (object instanceof List) {
      for (Object child : (List)object) {
        resolve(child, parentConfig);
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
          resolve(propertyValue, parentConfig);
        }
      }
    }
    catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
      throw new RuntimeException("Unable to get bean properties from '" + model.getClass().getName() + "'.", ex);
    }
  }

  private static Map<String, Object> resolveConfigurable(Configurable configurable, Map<String, Object> parentConfig) {
    Map<String, Object> mergedConfig = MapMerger.merge(configurable.getConfig(), parentConfig);

    configurable.setConfig(mergedConfig);

    return mergedConfig;
  }

}
