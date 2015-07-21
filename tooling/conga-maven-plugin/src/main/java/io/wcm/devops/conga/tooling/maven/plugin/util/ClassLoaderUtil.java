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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Utility methods for building class loaders.
 */
public final class ClassLoaderUtil {

  private ClassLoaderUtil() {
    // static methods only
  }

  /**
   * Build class loader from dependency of a maven project.
   * @param project Maven project
   * @return Class loader
   * @throws MojoExecutionException
   */
  public static ClassLoader buildDependencyClassLoader(MavenProject project) throws MojoExecutionException {
    try {
      List<URL> classLoaderUrls = new ArrayList<>();
      for (String path : project.getCompileClasspathElements()) {
        classLoaderUrls.add(new File(path).toURI().toURL());
      }
      return new URLClassLoader(classLoaderUrls.toArray(new URL[classLoaderUrls.size()]));
    }
    catch (MalformedURLException | DependencyResolutionRequiredException ex) {
      throw new MojoExecutionException("Unable to get classpath elements for class loader.", ex);
    }
  }

}
