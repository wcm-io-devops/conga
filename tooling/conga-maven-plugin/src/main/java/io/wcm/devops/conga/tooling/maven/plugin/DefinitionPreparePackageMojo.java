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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Packages the definitions in a ZIP file.
 */
@Mojo(name = "definition-prepare-package", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, requiresProject = true, threadSafe = true)
public class DefinitionPreparePackageMojo extends AbstractCongaMojo {

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

  @Parameter(property = "project", required = true, readonly = true)
  private MavenProject project;

  private ResourceLoader resourceLoader;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    resourceLoader = new ResourceLoader();

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
  }

  /**
   * Copy definitions and template files to classes folder to include them in JAR artifacte.
   * @param sourceDir Definition/Template source folder
   * @param rootOutputDir Classes root folder
   * @param parentTargetDir Parent target folder within classes folder
   * @param dirName Directory name
   * @throws IOException
   */
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