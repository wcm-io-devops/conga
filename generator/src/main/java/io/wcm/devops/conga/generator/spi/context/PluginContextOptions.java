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
package io.wcm.devops.conga.generator.spi.context;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import io.wcm.devops.conga.generator.UrlFileManager;
import io.wcm.devops.conga.generator.util.PluginManager;

/**
 * Parameters passed into all plugin contexts derived from {@link AbstractPluginContext}.
 */
public final class PluginContextOptions {

  private PluginManager pluginManager;
  private UrlFileManager urlFileManager;
  private Map<String, Map<String, Object>> valueProviderConfig;
  private Map<String, Map<String, Object>> genericPluginConfig = new HashMap<>();
  private Object containerContext;
  private Logger logger;

  /**
   * Gets plugin manager.
   * @return Plugin manager
   */
  public PluginManager getPluginManager() {
    return this.pluginManager;
  }

  /**
   * Sets plugin manager.
   * @param value Plugin manager
   * @return this
   */
  public PluginContextOptions pluginManager(PluginManager value) {
    this.pluginManager = value;
    return this;
  }

  /**
   * Gets URL file manager.
   * @return URL file manager
   */
  public UrlFileManager getUrlFileManager() {
    return this.urlFileManager;
  }

  /**
   * Sets URL file manager.
   * @param value URL file manager
   * @return this
   */
  public PluginContextOptions urlFileManager(UrlFileManager value) {
    this.urlFileManager = value;
    return this;
  }

  /**
   * Gets value provider configuration.
   * @return Value provider configuration
   */
  public Map<String, Map<String, Object>> getValueProviderConfig() {
    return this.valueProviderConfig;
  }

  /**
   * Sets value provider configuration.
   * @param value Value provider configuration
   * @return this
   */
  public PluginContextOptions valueProviderConfig(Map<String, Map<String, Object>> value) {
    this.valueProviderConfig = value;
    return this;
  }

  /**
   * Gets generic plugin configuration.
   * @return Generic plugin configuration
   */
  public Map<String, Map<String, Object>> getGenericPluginConfig() {
    return this.genericPluginConfig;
  }

  /**
   * Sets generic plugin configuration.
   * @param value Generic plugin configuration
   * @return this
   */
  public PluginContextOptions genericPluginConfig(Map<String, Map<String, Object>> value) {
    this.genericPluginConfig = value;
    return this;
  }

  /**
   * Gets container context.
   * @return Container-specific context object
   */
  public Object getContainerContext() {
    return containerContext;
  }

  /**
   * Sets container context.
   * @param value Container-specific context object
   * @return this
   */
  public PluginContextOptions containerContext(Object value) {
    containerContext = value;
    return this;
  }

  /**
   * Gets logger.
   * @return Logger
   */
  public Logger getLogger() {
    return logger;
  }

  /**
   * Sets logger.
   * @param value Logger
   * @return this
   */
  public PluginContextOptions logger(Logger value) {
    logger = value;
    return this;
  }

  /**
   * Initialize options with all fields from other options.
   * @param value Other options
   * @return this
   */
  public PluginContextOptions pluginContextOptions(PluginContextOptions value) {
    this.pluginManager = value.getPluginManager();
    this.urlFileManager = value.getUrlFileManager();
    this.valueProviderConfig = value.getValueProviderConfig();
    this.genericPluginConfig = value.getGenericPluginConfig();
    this.containerContext = value.containerContext;
    this.logger = value.getLogger();
    return this;
  }

}
