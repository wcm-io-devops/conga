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

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Iterates over all {@link Configurable} items in the object tree
 * and resolves all variables using {@link VariableMapResolver}.
 */
public final class VariableObjectTreeResolver {

  private VariableObjectTreeResolver() {
    // static methods only
  }

  /**
   * Resolve all variables.
   * @param object Model with {@link Configurable} instances at any nested level.
   */
  public static void resolve(Object object) {
    if (object instanceof Map) {
      for (Object child : ((Map)object).values()) {
        resolve(child);
      }
      return;
    }
    if (object instanceof List) {
      for (Object child : (List)object) {
        resolve(child);
      }
      return;
    }
    if (object instanceof Configurable) {
      Configurable configurable = (Configurable)object;
      resolveConfigurable(configurable);
    }
    resolveNestedObjects(object);
  }

  private static void resolveNestedObjects(Object model) {
    try {
      Map<String, String> description = BeanUtils.describe(model);
      for (String propertyName : description.keySet()) {
        Object propertyValue = PropertyUtils.getProperty(model, propertyName);
        if (!StringUtils.equals(propertyName, "class")) {
          resolve(propertyValue);
        }
      }
    }
    catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
      throw new RuntimeException("Unable to get bean properties from '" + model.getClass().getName() + "'.", ex);
    }
  }

  private static void resolveConfigurable(Configurable configurable) {
    Map<String, Object> resolvedconfig = VariableMapResolver.resolve(configurable.getConfig());
    configurable.setConfig(resolvedconfig);
  }

}
