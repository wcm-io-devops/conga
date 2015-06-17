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

import java.util.HashMap;
import java.util.Map;

/**
 * Iterates over all {@link Configurable} items in the object tree.
 * Configuration from parent objects is inherited to client objects.
 * Variables are not resolved though, this is done during configuration generation process.
 */
public final class ConfigInheritanceResolver extends AbstractConfigurableObjectTreeProcessor<Map<String, Object>> {

  private static final ConfigurableProcessor<Map<String, Object>> PROCESSOR = new ConfigurableProcessor<Map<String, Object>>() {
    @Override
    public Map<String, Object> process(Configurable configurable, Map<String, Object> parentConfig) {
      Map<String, Object> mergedConfig = MapMerger.merge(configurable.getConfig(), parentConfig);
      configurable.setConfig(mergedConfig);
      return mergedConfig;
    }
  };

  private ConfigInheritanceResolver() {
    // static methods only
  }

  /**
   * Inherit all configurations.
   * @param model Model with {@link Configurable} instances at any nested level.
   */
  public static void resolve(Object model) {
    new ConfigInheritanceResolver().process(model, PROCESSOR, new HashMap<>());
  }

}
