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

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import io.wcm.devops.conga.model.shared.Configurable;

/**
 * Iterates over all {@link Configurable} items in the object tree.
 * @param <T> Payload for processor
 */
abstract class AbstractConfigurableObjectTreeProcessor<T> {

  /**
   * Iterator over object tree an visit all {@link Configurable} instances. Payload
   * can be inherited from down the hierarchy levels.
   * @param object Root object of object tree
   */
  protected void process(Object object, ConfigurableProcessor<T> processor, T parentPayload) {
    if (object instanceof Map) {
      for (Object child : ((Map)object).values()) {
        process(child, processor, parentPayload);
      }
      return;
    }
    if (object instanceof List) {
      for (Object child : (List)object) {
        process(child, processor, parentPayload);
      }
      return;
    }
    T payload;
    if (object instanceof Configurable) {
      Configurable configurable = (Configurable)object;
      payload = processor.process(configurable, parentPayload);
    }
    else {
      payload = parentPayload;
    }
    resolveNestedObjects(object, processor, payload);
  }

  private void resolveNestedObjects(Object object, ConfigurableProcessor<T> processor, T parentPayload) {
    if (object == null || object.getClass().isEnum()) {
      return;
    }
    try {
      Map<String, String> description = BeanUtils.describe(object);
      for (String propertyName : description.keySet()) {
        Object propertyValue = PropertyUtils.getProperty(object, propertyName);
        if (!StringUtils.equals(propertyName, "class")) {
          process(propertyValue, processor, parentPayload);
        }
      }
    }
    catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
      throw new RuntimeException("Unable to get bean properties from '" + object.getClass().getName() + "'.", ex);
    }
  }

}
