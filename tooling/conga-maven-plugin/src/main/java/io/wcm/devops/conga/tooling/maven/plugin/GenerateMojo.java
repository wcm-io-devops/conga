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

import io.wcm.devops.conga.generator.Generator;
import io.wcm.devops.conga.resource.ResourceCollection;
import io.wcm.devops.conga.resource.ResourceLoader;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Generates configuration using CONGA generator.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresProject = true, threadSafe = true)
public class GenerateMojo extends AbstractMojo {

  /**
   * Source path with templates.
   */
  @Parameter(defaultValue = "${basedir}/src/templates")
  private String templateDir;

  /**
   * Source path with role definitions.
   */
  @Parameter(defaultValue = "${basedir}/src/roles")
  private String roleDir;

  /**
   * Source path with environment definitions.
   */
  @Parameter(defaultValue = "${basedir}/src/environments")
  private String environmentDir;

  /**
   * Target path for the generated configuration files.
   */
  @Parameter(defaultValue = "${project.build.directory}/configuration")
  private String target;

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

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    ResourceCollection templateDirectory = ResourceLoader.getResourceCollection(templateDir);
    ResourceCollection roleDirecotry = ResourceLoader.getResourceCollection(roleDir);
    ResourceCollection environmentDirecotry = ResourceLoader.getResourceCollection(environmentDir);
    File targetDirecotry = new File(target);

    Generator generator = new Generator(roleDirecotry, environmentDirecotry, templateDirectory, targetDirecotry);
    generator.setLogger(new MavenSlf4jLogFacade(getLog()));
    generator.setDeleteBeforeGenerate(deleteBeforeGenerate);
    generator.generate(environments);
  }

}
