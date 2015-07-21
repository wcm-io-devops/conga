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

import static io.wcm.devops.conga.model.util.DefaultUtil.defaultEmptyMap;
import io.wcm.devops.conga.model.util.MapExpander;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract {@link Configurable} implementation.
 */
public abstract class AbstractConfigurable extends AbstractModel implements Configurable {

  private Map<String, Object> config = new HashMap<>();

  /**
   * Defines a map of configuration parameters.
   * They are merged with the configuration parameters from the configuration inheritance tree.
   * @return Configuration parameter map
   */
  @Override
  public final Map<String, Object> getConfig() {
    return this.config;
  }

  /**
   * @param config Config
   */
  @Override
  public final void setConfig(Map<String, Object> config) {
    this.config = defaultEmptyMap(MapExpander.expand(config));
  }

}
