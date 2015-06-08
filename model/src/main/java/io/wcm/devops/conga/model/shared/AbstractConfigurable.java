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
package io.wcm.devops.conga.model.shared;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract {@link Configurable} implementation.
 */
public abstract class AbstractConfigurable implements Configurable {

  private Map<String, Object> config = new HashMap<>();
  private Map<String, Object> variables = new HashMap<>();
  private boolean resolved;

  @Override
  public final Map<String, Object> getConfig() {
    return this.config;
  }

  /**
   * @param config Config
   */
  public final void setConfig(Map<String, Object> config) {
    if (resolved) {
      throw new IllegalStateException("Model is already resolved.");
    }
    // Expand shortcut map entries with "." notation to nested maps
    this.config = MapExpander.expand(config);
  }

  @Override
  public final Map<String, Object> getVariables() {
    return this.variables;
  }

  /**
   * @param variables Variables
   */
  public final void setVariables(Map<String, Object> variables) {
    if (resolved) {
      throw new IllegalStateException("Model is already resolved.");
    }
    // Expand shortcut map entries with "." notation to nested maps
    this.variables = MapExpander.expand(variables);
  }

  @Override
  public final void resolved(Map<String, Object> resolvedConfig) {
    if (resolved) {
      throw new IllegalStateException("Model is already resolved.");
    }
    resolved = true;
    this.config = resolvedConfig;
    this.variables = null;
  }

  @Override
  public final boolean isResolved() {
    return resolved;
  }

}
