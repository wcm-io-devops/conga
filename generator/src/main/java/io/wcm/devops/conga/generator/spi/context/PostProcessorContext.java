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

import io.wcm.devops.conga.generator.util.PluginManager;

/**
 * Context for {@link io.wcm.devops.conga.generator.spi.PostProcessorPlugin}.
 */
public final class PostProcessorContext extends AbstractPluginContext<PostProcessorContext> {

  private Map<String, Object> options;
  private PluginManager pluginManager;

  /**
   * @return Post processor options
   */
  public Map<String, Object> getOptions() {
    return options;
  }

  /**
   * @param value Post processor options
   * @return this
   */
  public PostProcessorContext options(Map<String, Object> value) {
    options = value;
    return this;
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
  public PostProcessorContext pluginManager(PluginManager value) {
    this.pluginManager = value;
    return this;
  }

}
