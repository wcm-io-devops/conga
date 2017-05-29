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

import org.slf4j.Logger;

import io.wcm.devops.conga.generator.UrlFileManager;
import io.wcm.devops.conga.generator.util.PluginManager;

/**
 * Generic plugin context.
 */
public abstract class AbstractPluginContext<T> {

  private Logger logger;
  private PluginManager pluginManager;
  private UrlFileManager urlFileManager;

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
  @SuppressWarnings("unchecked")
  public T logger(Logger value) {
    logger = value;
    return (T)this;
  }

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
  @SuppressWarnings("unchecked")
  public T pluginManager(PluginManager value) {
    this.pluginManager = value;
    return (T)this;
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
  @SuppressWarnings("unchecked")
  public T urlFileManager(UrlFileManager value) {
    this.urlFileManager = value;
    return (T)this;
  }

}
