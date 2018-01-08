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
  private Map<String, Map<String, Object>> genericPluginConfig = new HashMap<>();
  private Logger logger;

  /**
   * @return Plugin manager
   */
  public PluginManager getPluginManager() {
    return this.pluginManager;
  }

  /**
   * @param value Plugin manager
   * @return this
   */
  public PluginContextOptions pluginManager(PluginManager value) {
    this.pluginManager = value;
    return this;
  }

  /**
   * @return URL file manager
   */
  public UrlFileManager getUrlFileManager() {
    return this.urlFileManager;
  }

  /**
   * @param value URL file manager
   * @return this
   */
  public PluginContextOptions urlFileManager(UrlFileManager value) {
    this.urlFileManager = value;
    return this;
  }

  /**
   * @return Generic plugin configuration
   */
  public Map<String, Map<String, Object>> getGenericPluginConfig() {
    return this.genericPluginConfig;
  }

  /**
   * @param value Generic plugin configuration
   * @return this
   */
  public PluginContextOptions genericPluginConfig(Map<String, Map<String, Object>> value) {
    this.genericPluginConfig = value;
    return this;
  }

  /**
   * @return Logger
   */
  public Logger getLogger() {
    return logger;
  }

  /**
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
    this.genericPluginConfig = value.getGenericPluginConfig();
    this.logger = value.getLogger();
    return this;
  }

}
