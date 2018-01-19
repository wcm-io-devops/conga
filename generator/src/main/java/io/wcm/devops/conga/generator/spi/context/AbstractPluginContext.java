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
package io.wcm.devops.conga.generator.spi.context;

import java.util.Map;

import org.slf4j.Logger;

import io.wcm.devops.conga.generator.UrlFileManager;
import io.wcm.devops.conga.generator.util.PluginManager;

/**
 * Generic plugin context.
 */
public abstract class AbstractPluginContext<T> {

  private PluginContextOptions pluginContextOptions = new PluginContextOptions();

  /**
   * @return Generic plugin context options
   */
  public final PluginContextOptions getPluginContextOptions() {
    return this.pluginContextOptions;
  }

  /**
   * @param value Generic plugin context options
   * @return this
   */
  @SuppressWarnings("unchecked")
  public final T pluginContextOptions(PluginContextOptions value) {
    this.pluginContextOptions = value;
    return (T)this;
  }

  /**
   * @return Plugin manager
   */
  public final PluginManager getPluginManager() {
    return pluginContextOptions.getPluginManager();
  }

  /**
   * @return URL file manager
   */
  public final UrlFileManager getUrlFileManager() {
    return pluginContextOptions.getUrlFileManager();
  }

  /**
   * @return Generic plugin configuration
   */
  public final Map<String, Map<String, Object>> getGenericPluginConfig() {
    return pluginContextOptions.getGenericPluginConfig();
  }

  /**
   * @return Container-specific context object
   */
  public Object getContainerContext() {
    return pluginContextOptions.getContainerContext();
  }

  /**
   * @return Logger
   */
  public final Logger getLogger() {
    return pluginContextOptions.getLogger();
  }

}
