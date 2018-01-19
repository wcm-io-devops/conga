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
package io.wcm.devops.conga.generator;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.google.common.collect.ImmutableMap;

import io.wcm.devops.conga.generator.spi.context.PluginContextOptions;
import io.wcm.devops.conga.generator.spi.context.UrlFilePluginContext;
import io.wcm.devops.conga.generator.util.ConfigInheritanceResolver;
import io.wcm.devops.conga.model.reader.ModelReader;
import io.wcm.devops.conga.resource.Resource;
import io.wcm.devops.conga.resource.ResourceCollection;

/**
 * Helper methods for loading resources/model files.
 */
final class ResourceLoaderUtil {

  private ResourceLoaderUtil() {
    // static methods only
  }

  /**
   * Build {@link ClassLoader} based on given list of dependency URLs.
   * @param classpathUrls Classpath urls
   * @return Resource loader
   */
  public static ClassLoader buildClassLoader(List<URL> classpathUrls) {
    return new URLClassLoader(classpathUrls.toArray(new URL[classpathUrls.size()]));
  }

  /**
   * Get classpath URLs from dependency urls defined in environment.
   * @param dependencyUrls Dependency URLs from environment
   * @param options Generator options
   * @return Classpath URLs from environment plus container classpath URLs
   */
  public static List<URL> getEnvironmentClasspathUrls(List<String> dependencyUrls, GeneratorOptions options) {

    UrlFilePluginContext urlFilePluginContext = new UrlFilePluginContext()
        .baseDir(options.getBaseDir())
        .resourceClassLoader(ResourceLoaderUtil.class.getClassLoader())
        .pluginContextOptions(new PluginContextOptions()
            .containerContext(options.getContainerContext()));
    UrlFileManager urlFileManager = new UrlFileManager(options.getPluginManager(), urlFilePluginContext);

    List<URL> classpathUrls = new ArrayList<>();
    for (String dependencyUrl : dependencyUrls) {
      try {
        classpathUrls.addAll(urlFileManager.getFileUrlsWithDependencies(dependencyUrl));
      }
      catch (IOException ex) {
        throw new GeneratorException("Unable to resolve: " + dependencyUrl, ex);
      }
    }
    classpathUrls.addAll(options.getContainerClasspathUrls());
    return classpathUrls;
  }

  /**
   * Read model files.
   * @param dirs Directories
   * @param reader Model reader
   * @return Parsed models
   * @param <T> Model type
   */
  public static <T> Map<String, T> readModels(List<ResourceCollection> dirs, ModelReader<T> reader) {
    Map<String, T> models = new HashMap<>();
    for (ResourceCollection dir : dirs) {
      for (Resource file : dir.getResources()) {
        if (reader.accepts(file)) {
          try {
            T model = reader.read(file);
            ConfigInheritanceResolver.resolve(model);
            models.put(FilenameUtils.getBaseName(file.getName()), model);
          }
          /*CHECKSTYLE:OFF*/ catch (Exception ex) { /*CHECKSTYLE:ON*/
            throw new GeneratorException("Unable to read definition: " + file.getCanonicalPath(), ex);
          }
        }
      }
    }
    return ImmutableMap.copyOf(models);
  }

}
