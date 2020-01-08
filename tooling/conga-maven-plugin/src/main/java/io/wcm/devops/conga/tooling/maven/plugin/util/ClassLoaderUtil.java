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
package io.wcm.devops.conga.tooling.maven.plugin.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.google.common.collect.ImmutableMap;

import io.wcm.devops.conga.generator.spi.context.PluginContextOptions;
import io.wcm.devops.conga.generator.spi.context.ValueProviderGlobalContext;
import io.wcm.devops.conga.generator.util.VariableMapResolver;
import io.wcm.devops.conga.generator.util.VariableStringResolver;

/**
 * Utility methods for managing classpath and class loaders.
 */
public final class ClassLoaderUtil {

  private ClassLoaderUtil() {
    // static methods only
  }

  /**
   * Build {@link ClassLoader} based on given list of dependency URLs.
   * @param classpathUrls Classpath urls
   * @return Resource loader
   */
  public static ClassLoader buildClassLoader(List<URL> classpathUrls) {
    return new URLClassLoader(classpathUrls.toArray(new URL[0]));
  }

  /**
   * Build class loader from dependency of a maven project.
   * @param project Maven project
   * @return Class loader
   * @throws MojoExecutionException Mojo execution exception
   */
  public static List<URL> getMavenProjectClasspathUrls(MavenProject project) throws MojoExecutionException {
    try {
      List<URL> classpathUrls = new ArrayList<>();
      for (String path : project.getCompileClasspathElements()) {
        classpathUrls.add(new File(path).toURI().toURL());
      }
      for (Resource resource : project.getResources()) {
        classpathUrls.add(new File(resource.getDirectory()).toURI().toURL());
      }
      return classpathUrls;
    }
    catch (MalformedURLException | DependencyResolutionRequiredException ex) {
      throw new MojoExecutionException("Unable to get classpath elements for class loader.", ex);
    }
  }

  /**
   * Resolves an environment dependency URL.
   * @param dependencyUrl Dependeny URL
   * @param pluginContextOptions Plugin context options
   * @return Resolved dependency URL.
   */
  public static String resolveDependencyUrl(String dependencyUrl, PluginContextOptions pluginContextOptions) {

    ValueProviderGlobalContext valueProviderGlobalContext = new ValueProviderGlobalContext()
        .pluginContextOptions(pluginContextOptions);
    VariableMapResolver variableMapResolver = new VariableMapResolver(valueProviderGlobalContext);
    VariableStringResolver variableStringResolver = new VariableStringResolver(valueProviderGlobalContext, variableMapResolver);

    // resolver variables without config map - thus supporting only value providers with external values
    return variableStringResolver.resolveString(dependencyUrl, ImmutableMap.of());
  }

}
