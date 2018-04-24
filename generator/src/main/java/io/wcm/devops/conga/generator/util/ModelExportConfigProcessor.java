/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2018 wcm.io
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
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import io.wcm.devops.conga.generator.ContextPropertiesBuilder;
import io.wcm.devops.conga.generator.spi.ValueEncryptionPlugin;
import io.wcm.devops.conga.generator.spi.context.PluginContextOptions;
import io.wcm.devops.conga.generator.spi.context.ValueEncryptionContext;

/**
 * Processes map of CONGA configuration parameters before serializing them in a model export file.
 * Removes CONGA-defined context variables, and encrypts sensitive parameter values if required.
 */
public class ModelExportConfigProcessor {

  private final ValueEncryptionPlugin valueEncryptionPlugin;
  private final ValueEncryptionContext valueEncryptionContext;
  private final Set<String> sensitiveConfigParameters;

  /**
   * @param pluginContextOptions Plugin context options
   * @param sensitiveConfigParameters Sensitive config parameter names
   */
  public ModelExportConfigProcessor(PluginContextOptions pluginContextOptions, Set<String> sensitiveConfigParameters) {
    this.valueEncryptionPlugin = getFirstEnabledValueEncryptionPlugin(pluginContextOptions.getPluginManager());
    this.valueEncryptionContext = new ValueEncryptionContext()
        .pluginContextOptions(pluginContextOptions);
    this.sensitiveConfigParameters = sensitiveConfigParameters;
  }

  private ValueEncryptionPlugin getFirstEnabledValueEncryptionPlugin(PluginManager pluginManager) {
    return pluginManager.getAll(ValueEncryptionPlugin.class)
        .stream()
        .filter(ValueEncryptionPlugin::isEnabled)
        .findFirst().orElse(null);
  }

  /**
   * Apply processor.
   * @param config Configuration
   * @return Processed configuration
   */
  public Map<String, Object> apply(Map<String, Object> config) {
    // remove all CONGA-defined context variables
    Map<String, Object> processedConfig = ContextPropertiesBuilder.removeContextVariables(config);

    // encrypted sensitive parameter values
    processedConfig = encryptSensitiveValues(processedConfig, null);

    return processedConfig;
  }

  private Map<String, Object> encryptSensitiveValues(Map<String, Object> config, String prefix) {
    Map<String, Object> map = new HashMap<>(config);
    for (String key : config.keySet()) {
      String parameterName = StringUtils.defaultString(prefix) + key;
      Object value = map.get(key);
      map.put(key, encryptSensitiveValue(parameterName, value));
    }
    return map;
  }

  @SuppressWarnings("unchecked")
  private Object encryptSensitiveValue(String parameterName, Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof Map) {
      return encryptSensitiveValues((Map<String, Object>)value, parameterName + ".");
    }
    else if (value instanceof List) {
      List<Object> list = new ArrayList<>();
      for (Object itemValue : (List)value) {
        list.add(encryptSensitiveValue(parameterName, itemValue));
      }
      return list;
    }
    else {
      return encryptValueIfRequired(parameterName, value);
    }
  }

  private Object encryptValueIfRequired(String parameterName, Object value) {
    if (value != null
        && valueEncryptionPlugin != null
        && sensitiveConfigParameters.contains(parameterName)) {
      return valueEncryptionPlugin.encrypt(parameterName, value, valueEncryptionContext);
    }
    return value;
  }

}
