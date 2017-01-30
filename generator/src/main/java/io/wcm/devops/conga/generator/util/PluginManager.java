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
package io.wcm.devops.conga.generator.util;

import java.util.List;
import java.util.ServiceLoader;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.Plugin;

/**
 * Manages registered plugin. Plugins are registered using the {@link ServiceLoader} concept.
 */
public interface PluginManager {

  /**
   * Get plugin instance.
   * @param name Plugin name
   * @param pluginClass Plugin class
   * @param <T> Plugin type
   * @return Plugin instance.
   * @throws GeneratorException When plugin is not found.
   */
  <T extends Plugin> T get(String name, Class<T> pluginClass) throws GeneratorException;

  /**
   * Get all plugin instance.
   * @param pluginClass Plugin class
   * @param <T> Plugin type
   * @return Plugin instances.
   * @throws GeneratorException When plugin could not be loaded.
   */
  <T extends Plugin> List<T> getAll(Class<T> pluginClass) throws GeneratorException;

}
