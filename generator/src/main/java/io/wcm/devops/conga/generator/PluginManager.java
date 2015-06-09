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
package io.wcm.devops.conga.generator;

import io.wcm.devops.conga.generator.spi.Plugin;

import java.util.List;
import java.util.ServiceLoader;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;

/**
 * Manages registered plugin. Plugins are registered using the {@link ServiceLoader} concept.
 */
public final class PluginManager {

  private final LoadingCache<Class<Plugin>, SortedMap<String, Plugin>> pluginCache =
      CacheBuilder.newBuilder().build(new CacheLoader<Class<Plugin>, SortedMap<String, Plugin>>() {
        @Override
        public SortedMap<String, Plugin> load(Class<Plugin> pluginClass) throws Exception {
          ServiceLoader<Plugin> loadedPlugins = ServiceLoader.load(pluginClass);
          SortedMap<String, Plugin> pluginMap = new TreeMap<>();
          for (Plugin plugin : loadedPlugins) {
            if (pluginMap.containsKey(plugin.getName())) {
              throw new GeneratorException("Plugin name '" + plugin.getName() + "' is not unique. "
                  + pluginMap.get(plugin.getName()) + " clashes with " + plugin + ".");
            }
            pluginMap.put(plugin.getName(), plugin);
          }
          return pluginMap;
        }
      });

  /**
   * Get plugin instance.
   * @param name Plugin name
   * @param pluginClass Plugin class
   * @return Plugin instance.
   * @throws GeneratorException When plugin is not found.
   */
  @SuppressWarnings("unchecked")
  public <T extends Plugin> T get(String name, Class<T> pluginClass) throws GeneratorException {
    try {
      Plugin plugin = pluginCache.get((Class<Plugin>)pluginClass).get(name);
      if (plugin == null) {
        throw new GeneratorException(pluginClass.getSimpleName() + " not found: '" + name + "'");
      }
      return (T)plugin;
    }
    catch (ExecutionException ex) {
      throw new GeneratorException("Untable to build plugin cache for " + pluginClass.getName(), ex);
    }
  }

  /**
   * Get all plugin instance.
   * @param pluginClass Plugin class
   * @return Plugin instances.
   * @throws GeneratorException When plugin could not be loaded.
   */
  @SuppressWarnings("unchecked")
  public <T extends Plugin> List<T> getAll(Class<T> pluginClass) throws GeneratorException {
    try {
      return (List<T>)ImmutableList.copyOf(pluginCache.get((Class<Plugin>)pluginClass).values());
    }
    catch (ExecutionException ex) {
      throw new GeneratorException("Untable to build plugin cache for " + pluginClass.getName(), ex);
    }
  }

}
