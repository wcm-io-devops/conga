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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

import com.google.common.collect.ImmutableSet;

import io.wcm.devops.conga.resource.ResourceLoader;

/**
 * Packages the generated configurations in a ZIP file.
 */
@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true)
public class PackageMojo extends AbstractCongaMojo {

  /**
   * Selected environments to generate.
   */
  @Parameter
  private String[] environments;

  @Parameter(property = "project", required = true, readonly = true)
  private MavenProject project;

  @Parameter(defaultValue = "${session}", required = true, readonly = true)
  private MavenSession session;

  @Component
  protected MavenProjectHelper projectHelper;

  @Component(role = Archiver.class, hint = "zip")
  private ZipArchiver zipArchiver;

  private ResourceLoader resourceLoader;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    resourceLoader = new ResourceLoader();

    Set<String> selectedEnvironments;
    if (environments != null && environments.length > 0) {
      selectedEnvironments = ImmutableSet.copyOf(environments);
    }
    else {
      selectedEnvironments = null;
    }

    // collect configuration environment directories
    File configRootDir = getTargetDir();
    List<File> environmentDirs = Arrays.stream(configRootDir.listFiles())
        .filter(file -> file.isDirectory())
        .filter(dir -> selectedEnvironments == null || selectedEnvironments.contains(dir.getName()))
        .collect(Collectors.toList());

    // generate an ZIP artifact with generated configurations for each environment
    for (File environmentDir : environmentDirs) {

      // classifier is environment name
      // if current project is not a config project, prefix the classifier
      String classifier = environmentDir.getName();
      if (!StringUtils.equals(project.getPackaging(), PACKAGING_CONFIGURATION)) {
        classifier = CLASSIFIER_CONFIGURATION + "." + classifier;
      }

      // build ZIP artifact
      File outputFile = buildZipFile(environmentDir, classifier);

      // attach ZIP artifact
      projectHelper.attachArtifact(project, outputFile, classifier);

    }

  }

  /**
   * Build JAR file with definitions.
   * @param contentDirectory Content directory for JAR file
   * @return JAR file
   * @throws MojoExecutionException
   */
  private File buildZipFile(File contentDirectory, String classifier) throws MojoExecutionException {
    File zipFile = new File(project.getBuild().getDirectory(), buildZipFileName(classifier));

    zipArchiver.addDirectory(contentDirectory);
    zipArchiver.setDestFile(zipFile);
    try {
      zipArchiver.createArchive();
    }
    catch (ArchiverException | IOException ex) {
      throw new MojoExecutionException("Unable to build file " + zipFile.getPath() + ": " + ex.getMessage(), ex);
    }

    return zipFile;
  }

  private String buildZipFileName(String classifier) {
    StringBuilder sb = new StringBuilder();
    sb.append(project.getBuild().getFinalName());
    sb.append("-").append(classifier);
    sb.append(".").append(FILE_EXTENSION_CONFIGURATION);
    return sb.toString();
  }

  @Override
  protected ResourceLoader getResourceLoader() {
    return resourceLoader;
  }

}
