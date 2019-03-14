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

import io.wcm.devops.conga.generator.util.FileUtil;

/**
 * Packages the generated configurations in a ZIP file.
 */
@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true)
public class PackageMojo extends AbstractCongaMojo {

  /**
   * Selected environments to generate.
   */
  @Parameter(property = "conga.environments")
  private String[] environments;

  /**
   * If set to true (default) a separate ZIP artifact is generated per environment.
   * Otherwise a single ZIP containing all environments in sub directories is created.
   */
  @Parameter(defaultValue = "true")
  private boolean artifactPerEnvironment;

  @Parameter(property = "project", required = true, readonly = true)
  private MavenProject project;

  @Parameter(property = "session", readonly = true, required = true)
  private MavenSession mavenSession;

  @Component
  protected MavenProjectHelper projectHelper;

  @Component(role = Archiver.class, hint = "zip")
  private ZipArchiver zipArchiver;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
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

    if (artifactPerEnvironment) {
      // generate an ZIP artifact with generated configurations for each environment
      for (File environmentDir : environmentDirs) {

        // classifier is environment name
        // if current project is not a config project, prefix the classifier
        String classifier = environmentDir.getName();
        if (!StringUtils.equals(project.getPackaging(), PACKAGING_CONFIGURATION)) {
          classifier = CLASSIFIER_CONFIGURATION + "-" + classifier;
        }
        validateClassifier(classifier);

        // build ZIP artifact
        File outputFile = buildZipFile(environmentDir, classifier);

        // attach ZIP artifact
        projectHelper.attachArtifact(project, outputFile, classifier);

      }
    }
    else {
      // generate an ZIP artifact containing all environments
      String classifier = null;
      if (!StringUtils.equals(project.getPackaging(), PACKAGING_CONFIGURATION)) {
        classifier = CLASSIFIER_CONFIGURATION;
      }
      validateClassifier(classifier);

      File outputFile = buildZipFile(configRootDir, classifier);
      // set or attach ZIP artifact
      if (StringUtils.equals(project.getPackaging(), PACKAGING_CONFIGURATION)) {
        project.getArtifact().setFile(outputFile);
      }
      else {
        projectHelper.attachArtifact(project, outputFile, CLASSIFIER_CONFIGURATION);
      }
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

    String basePath = toZipDirectoryPath(contentDirectory);
    addZipDirectory(basePath, contentDirectory);
    zipArchiver.setDestFile(zipFile);
    try {
      zipArchiver.createArchive();
    }
    catch (ArchiverException | IOException ex) {
      throw new MojoExecutionException("Unable to build file " + zipFile.getPath() + ": " + ex.getMessage(), ex);
    }

    return zipFile;
  }

  /**
   * Recursive through all directory and add file to zipArchiver.
   * This is used instead of zipArchiver.addDirectory to make sure for symlinks the target of the symlink
   * are included rather than the symlink information itself.
   * @param basePath Base path
   * @param directory Directory to include
   */
  private void addZipDirectory(String basePath, File directory) {
    String directoryPath = toZipDirectoryPath(directory);
    if (StringUtils.startsWith(directoryPath, basePath)) {
      String relativeDirectoryPath = StringUtils.substring(directoryPath, basePath.length());
      for (File file : directory.listFiles()) {
        if (file.isDirectory()) {
          addZipDirectory(basePath, file);
        }
        else {
          zipArchiver.addFile(file, relativeDirectoryPath + file.getName());
        }
      }
    }
  }

  private String toZipDirectoryPath(File directory) {
    String canoncialPath = FileUtil.getCanonicalPath(directory);
    return StringUtils.replace(canoncialPath, "\\", "/") + "/";
  }

  private String buildZipFileName(String classifier) {
    StringBuilder sb = new StringBuilder();
    sb.append(project.getBuild().getFinalName());
    if (StringUtils.isNotBlank(classifier)) {
      sb.append("-").append(classifier);
    }
    sb.append(".").append(FILE_EXTENSION_CONFIGURATION);
    return sb.toString();
  }

  private void validateClassifier(String classifier) throws MojoExecutionException {
    // classifier should not contain dots to make sure separation from extension/packaging types is not affected
    if (StringUtils.contains(classifier, ".")) {
      throw new MojoExecutionException("Classifier must not contain dots: " + classifier);
    }
  }

}
