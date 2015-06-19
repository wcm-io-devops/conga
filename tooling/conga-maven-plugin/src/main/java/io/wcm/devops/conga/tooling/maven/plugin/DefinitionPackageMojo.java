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

import static io.wcm.devops.conga.tooling.maven.plugin.BuildConstants.CLASSIFIER_DEFINITION;
import static io.wcm.devops.conga.tooling.maven.plugin.BuildConstants.FILE_EXTENSION_DEFINITION;
import static io.wcm.devops.conga.tooling.maven.plugin.BuildConstants.PACKAGING_DEFINITION;
import io.wcm.devops.conga.resource.Resource;
import io.wcm.devops.conga.resource.ResourceCollection;
import io.wcm.devops.conga.resource.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
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
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;

/**
 * Packages the definitions in a ZIP file.
 */
@Mojo(name = "definition-package", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true)
public class DefinitionPackageMojo extends AbstractCongaMojo {

  /**
   * Target directory in JAR file for roles
   */
  public static final String ROLES_DIR = "CONGA-INF/roles";

  /**
   * Target directory in JAR file for roles
   */
  public static final String TEMPLATES_DIR = "CONGA-INF/templates";

  /**
   * Target directory in JAR file for roles
   */
  public static final String ENVIRONMENTS_DIR = "CONGA-INF/environments";

  /**
   * The archive configuration to use.
   * See <a href="http://maven.apache.org/shared/maven-archiver/index.html">Maven Archiver Reference</a>.
   */
  @Parameter
  private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

  @Parameter(property = "project", required = true, readonly = true)
  private MavenProject project;

  @Parameter(defaultValue = "${session}", readonly = true, required = true)
  private MavenSession session;

  @Component
  protected MavenProjectHelper projectHelper;

  @Component(role = Archiver.class, hint = "jar")
  private JarArchiver jarArchiver;

  private ResourceLoader resourceLoader;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    resourceLoader = new ResourceLoader();

    // copy definitions to classes dir
    File definitionDir = copyDefinitions();

    // build JAR artifact
    File outputFile = buildJarFile(definitionDir);

    // set or attach JAR artifact
    if (StringUtils.equals(project.getPackaging(), PACKAGING_DEFINITION)) {
      project.getArtifact().setFile(outputFile);
    }
    else {
      projectHelper.attachArtifact(project, outputFile, CLASSIFIER_DEFINITION);
    }
  }

  /**
   * Build JAR file with definitions.
   * @param contentDirectory Content directory for JAR file
   * @return JAR file
   * @throws MojoExecutionException
   */
  private File buildJarFile(File contentDirectory) throws MojoExecutionException {
    File jarFile = new File(project.getBuild().getDirectory(),
        project.getBuild().getFinalName() + "." + FILE_EXTENSION_DEFINITION);

    MavenArchiver archiver = new MavenArchiver();
    archiver.setArchiver(jarArchiver);
    archiver.setOutputFile(jarFile);
    archive.setForced(true);

    archiver.getArchiver().addDirectory(contentDirectory);
    try {
      archiver.createArchive(session, project, archive);
    }
    catch (ArchiverException | ManifestException | IOException | DependencyResolutionRequiredException ex) {
      throw new MojoExecutionException("Unable to build JAR file " + jarFile.getPath() + ": " + ex.getMessage(), ex);
    }

    return jarFile;
  }

  /**
   * Copy definitions and template files to classes folder to include them in JAR artifact.
   * @throws MojoExecutionException
   */
  private File copyDefinitions() throws MojoExecutionException {
    File outputDir = new File(project.getBuild().getOutputDirectory());
    if (!outputDir.exists()) {
      outputDir.mkdirs();
    }

    ResourceCollection roleDir = getRoleDir();
    ResourceCollection templateDir = getTemplateDir();
    ResourceCollection environmentDir = getEnvironmentDir();

    // copy definitions
    try {
      copyDefinitions(roleDir, outputDir, outputDir, ROLES_DIR);
      copyDefinitions(templateDir, outputDir, outputDir, TEMPLATES_DIR);
      copyDefinitions(environmentDir, outputDir, outputDir, ENVIRONMENTS_DIR);
    }
    catch (IOException ex) {
      throw new MojoExecutionException("Unable to copy definitions:" + ex.getMessage(), ex);
    }

    return outputDir;
  }

  private void copyDefinitions(ResourceCollection sourceDir, File rootOutputDir, File parentTargetDir, String dirName) throws IOException {
    if (!sourceDir.exists()) {
      return;
    }
    List<Resource> files = sourceDir.getResources();
    List<ResourceCollection> folders = sourceDir.getResourceCollections();
    if (files.isEmpty() && folders.isEmpty()) {
      return;
    }

    File targetDir = new File(parentTargetDir, dirName);
    if (!targetDir.exists()) {
      targetDir.mkdirs();
    }

    for (Resource file : files) {
      File targetFile = new File(targetDir, file.getName());

      getLog().info("Include " + getPathForLog(rootOutputDir, targetFile));

      if (targetFile.exists()) {
        targetFile.delete();
      }
      try (InputStream is = file.getInputStream()) {
        byte[] data = IOUtils.toByteArray(is);
        FileUtils.writeByteArrayToFile(targetFile, data);
      }
    }

    for (ResourceCollection folder : folders) {
      copyDefinitions(folder, rootOutputDir, targetDir, folder.getName());
    }
  }

  private String getPathForLog(File rootOutputDir, File file) throws IOException {
    String path = unifySlashes(file.getCanonicalPath());
    String rootPath = unifySlashes(rootOutputDir.getCanonicalPath()) + "/";
    return StringUtils.substringAfter(path, rootPath);
  }

  private String unifySlashes(String path) {
    return StringUtils.replace(path, "\\", "/");
  }

  @Override
  protected ResourceLoader getResourceLoader() {
    return resourceLoader;
  }

}
