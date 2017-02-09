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

import java.util.HashMap;
import java.util.Map;

import io.wcm.devops.conga.generator.ContextPropertiesBuilder;
import io.wcm.devops.conga.model.shared.Configurable;

/**
 * Iterates over all {@link Configurable} items in the object tree
 * and resolves all variables using {@link VariableMapResolver}.
 */
public final class VariableObjectTreeResolver extends AbstractConfigurableObjectTreeProcessor<Object> {

  // payload not used for this processor
  private static final ConfigurableProcessor<Object> PROCESSOR = new ConfigurableProcessor<Object>() {
    @Override
    public Object process(Configurable configurable, Object payload) {
      Map<String, Object> config = new HashMap<>(configurable.getConfig());

      // add all context variables with empty value so references to them do not lead to "unknown variable" errors
      config.putAll(ContextPropertiesBuilder.getEmptyContextVariables());

      Map<String, Object> resolvedconfig = VariableMapResolver.resolve(config);
      configurable.setConfig(resolvedconfig);
      return null;
    }
  };

  private VariableObjectTreeResolver() {
    // static methods only
  }

  /**
   * Resolve all variables.
   * @param object Model with {@link Configurable} instances at any nested level.
   */
  public static void resolve(Object object) {
    new VariableObjectTreeResolver().process(object, PROCESSOR, null);
  }

}
