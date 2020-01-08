/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 wcm.io
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
package io.wcm.devops.conga.generator.spi.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Global context for all {@link io.wcm.devops.conga.generator.spi.ValueProviderPlugin} implementations.
 */
public final class ValueProviderGlobalContext extends AbstractPluginContext<ValueProviderGlobalContext> {

  private final Map<String, Object> globalValueProviderCache = new HashMap<>();

  /**
   * Parameter name in value provider configuration to specify the value provider plugin name.
   * If not defined, the value provider name itself is used as plugin name.
   */
  public static final String PARAM_PLUGIN_NAME = "_plugin_";

  /**
   * @return Configuration for value providers.
   *         The outer map uses the value provider plugin name as key, the inner map contain the config properties.
   *         Never null.
   */
  public Map<String, Map<String, Object>> getValueProviderConfig() {
    Map<String, Map<String, Object>> value = this.getPluginContextOptions().getValueProviderConfig();
    if (value == null) {
      value = Collections.emptyMap();
    }
    return value;
  }

  /**
   * @param valueProviderName Value provider name
   * @return Configuration for the given value provider. Never null.
   */
  public Map<String, Object> getValueProviderConfig(String valueProviderName) {
    Map<String, Object> config = getValueProviderConfig().get(valueProviderName);
    if (config == null) {
      return Collections.emptyMap();
    }
    return config;
  }

  /**
   * Get global cache map used by all value provider plugin implementations.
   * @return Global cache map
   */
  public Map<String, Object> getGlobalValueProviderCache() {
    return globalValueProviderCache;
  }

}
