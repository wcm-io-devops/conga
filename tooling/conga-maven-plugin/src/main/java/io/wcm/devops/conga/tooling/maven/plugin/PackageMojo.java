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

import static io.wcm.devops.conga.tooling.maven.plugin.BuildConstants.CLASSIFIER_CONFIGURATION;
import static io.wcm.devops.conga.tooling.maven.plugin.BuildConstants.FILE_EXTENSION_CONFIGURATION;
import static io.wcm.devops.conga.tooling.maven.plugin.BuildConstants.PACKAGING_CONFIGURATION;
import io.wcm.devops.conga.resource.ResourceLoader;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.zeroturnaround.zip.ZipUtil;

/**
 * Packages the generated configurations in a ZIP file.
 */
@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true)
public class PackageMojo extends AbstractCongaMojo {

  @Parameter(property = "project", required = true, readonly = true)
  private MavenProject project;

  @Component
  protected MavenProjectHelper projectHelper;

  private ResourceLoader resourceLoader;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    resourceLoader = new ResourceLoader();

    File configRootDir = getTargetDir();
    File outputFile = new File(project.getBuild().getDirectory(),
        project.getBuild().getFinalName() + "." + FILE_EXTENSION_CONFIGURATION);
    outputFile.getParentFile().mkdirs();

    // pack generated configuration in ZIP file
    getLog().info("Package " + outputFile.getName());
    ZipUtil.pack(configRootDir, outputFile);

    // set or attach ZIP artifact
    if (StringUtils.equals(project.getPackaging(), PACKAGING_CONFIGURATION)) {
      project.getArtifact().setFile(outputFile);
    }
    else {
      projectHelper.attachArtifact(project, outputFile, CLASSIFIER_CONFIGURATION);
    }
  }

  @Override
  protected ResourceLoader getResourceLoader() {
    return resourceLoader;
  }

}
