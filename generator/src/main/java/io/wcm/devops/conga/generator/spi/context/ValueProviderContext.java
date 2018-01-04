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

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * Context for a slinge {@link io.wcm.devops.conga.generator.spi.ValueProviderPlugin} instance.
 */
public final class ValueProviderContext extends AbstractPluginContext<ValueProviderContext> {

  private ValueProviderGlobalContext valueProviderGlobalContext;
  private String valueProviderName;

  /**
   * @param context Global value provider context
   * @return this
   */
  public ValueProviderContext valueProviderGlobalContext(ValueProviderGlobalContext context) {
    this.valueProviderGlobalContext = context;
    return this
        .logger(context.getLogger())
        .pluginManager(context.getPluginManager())
        .urlFileManager(context.getUrlFileManager());
  }

  /**
   * @param value Value provider name
   * @return this
   */
  public ValueProviderContext valueProviderName(String value) {
    this.valueProviderName = value;
    return this;
  }

  /**
   * @return Value provider name
   */
  public String getValueProviderName() {
    return this.valueProviderName;
  }

  /**
   * @param key Configuration parameter name
   * @return Configuration for the current value provider. Never null.
   */
  public Object getValueProviderConfig(String key) {
    Map<String, Object> config = valueProviderGlobalContext.getValueProviderConfig(valueProviderName);
    if (config == null) {
      config = ImmutableMap.of();
    }
    return config.get(key);
  }

  /**
   * Get cache object for the current value provider.
   * @return Cache object or null if none was set yet
   */
  public Object getValueProviderCache() {
    return valueProviderGlobalContext.getGlobalValueProviderCache().get(valueProviderName);
  }

  /**
   * Set cache object for the current value provider.
   * @param object Cache object
   */
  public void setValueProviderCache(Object object) {
    valueProviderGlobalContext.getGlobalValueProviderCache().put(valueProviderName, object);
  }

}
