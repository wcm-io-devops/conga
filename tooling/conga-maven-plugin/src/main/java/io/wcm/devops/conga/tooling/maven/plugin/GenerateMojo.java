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
package io.wcm.devops.conga.tooling.maven.plugin;

import static io.wcm.devops.conga.tooling.maven.plugin.BuildConstants.CLASSPATH_ENVIRONMENTS_DIR;
import static io.wcm.devops.conga.tooling.maven.plugin.BuildConstants.CLASSPATH_ROLES_DIR;
import static io.wcm.devops.conga.tooling.maven.plugin.BuildConstants.CLASSPATH_TEMPLATES_DIR;
import io.wcm.devops.conga.generator.Generator;
import io.wcm.devops.conga.resource.ResourceCollection;
import io.wcm.devops.conga.resource.ResourceLoader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.google.common.collect.ImmutableList;

/**
 * Generates configuration using CONGA generator.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresProject = true, threadSafe = true,
requiresDependencyResolution = ResolutionScope.COMPILE)
public class GenerateMojo extends AbstractCongaMojo {

  /**
   * Selected environments to generate.
   */
  @Parameter
  private String[] environments;

  /**
   * Delete folders of environments before generating the new files.
   */
  @Parameter(defaultValue = "false")
  private boolean deleteBeforeGenerate;

  @Parameter(property = "project", required = true, readonly = true)
  private MavenProject project;

  private ResourceLoader resourceLoader;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    resourceLoader = new ResourceLoader(buildDependencyClassLoader());

    List<ResourceCollection> roleDirs = ImmutableList.of(getRoleDir(),
        getResourceLoader().getResourceCollection(ResourceLoader.CLASSPATH_PREFIX + CLASSPATH_ROLES_DIR));
    List<ResourceCollection> templateDirs = ImmutableList.of(getTemplateDir(),
        getResourceLoader().getResourceCollection(ResourceLoader.CLASSPATH_PREFIX + CLASSPATH_TEMPLATES_DIR));
    List<ResourceCollection> environmentDirs = ImmutableList.of(getEnvironmentDir(),
        getResourceLoader().getResourceCollection(ResourceLoader.CLASSPATH_PREFIX + CLASSPATH_ENVIRONMENTS_DIR));

    Generator generator = new Generator(roleDirs, templateDirs, environmentDirs, getTargetDir());
    generator.setLogger(new MavenSlf4jLogFacade(getLog()));
    generator.setDeleteBeforeGenerate(deleteBeforeGenerate);
    generator.generate(environments);
  }

  /**
   * Build class loader from dependency of current maven project - to allow referencing definitions
   * from artifacts build with "conga-definition" package type.
   * @return Class loader
   * @throws MojoExecutionException
   */
  private ClassLoader buildDependencyClassLoader() throws MojoExecutionException {
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

  @Override
  protected ResourceLoader getResourceLoader() {
    return resourceLoader;
  }

}
