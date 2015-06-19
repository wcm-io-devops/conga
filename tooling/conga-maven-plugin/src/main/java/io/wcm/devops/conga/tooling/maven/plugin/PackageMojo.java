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

import io.wcm.devops.conga.resource.ResourceLoader;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.zeroturnaround.zip.ZipUtil;

/**
 * Packages the generated configurations in a ZIP file.
 */
@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true)
public class PackageMojo extends AbstractCongaMojo {

  @Parameter(property = "project", required = true, readonly = true)
  private MavenProject project;

  private ResourceLoader resourceLoader;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    resourceLoader = new ResourceLoader();

    File configRootDir = getTargetDir();
    File outputFile = new File(project.getBuild().getDirectory(), project.getBuild().getFinalName() + ".zip");
    outputFile.getParentFile().mkdirs();

    getLog().info("Package " + outputFile.getName());

    ZipUtil.pack(configRootDir, outputFile);

    project.getArtifact().setFile(outputFile);
  }

  @Override
  protected ResourceLoader getResourceLoader() {
    return resourceLoader;
  }

}
