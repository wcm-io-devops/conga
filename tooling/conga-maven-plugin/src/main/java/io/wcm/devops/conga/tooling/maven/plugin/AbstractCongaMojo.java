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

import static io.wcm.devops.conga.generator.GeneratorOptions.CLASSPATH_ENVIRONMENTS_DIR;
import static io.wcm.devops.conga.generator.GeneratorOptions.CLASSPATH_ROLES_DIR;
import static io.wcm.devops.conga.generator.GeneratorOptions.CLASSPATH_TEMPLATES_DIR;
import static io.wcm.devops.conga.tooling.maven.plugin.BuildConstants.CLASSIFIER_DEFINITION;
import static io.wcm.devops.conga.tooling.maven.plugin.BuildConstants.FILE_EXTENSION_DEFINITION;
import static io.wcm.devops.conga.tooling.maven.plugin.BuildConstants.PACKAGING_CONFIGURATION;
import static io.wcm.devops.conga.tooling.maven.plugin.BuildConstants.PACKAGING_DEFINITION;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.wcm.devops.conga.generator.export.ModelExport;
import io.wcm.devops.conga.resource.Resource;
import io.wcm.devops.conga.resource.ResourceCollection;
import io.wcm.devops.conga.resource.ResourceLoader;
import io.wcm.devops.conga.tooling.maven.plugin.util.PluginConfigUtil;

/**
 * Common features for all Mojos.
 */
abstract class AbstractCongaMojo extends AbstractMojo {

  /**
   * Source path with templates.
   */
  @Parameter(defaultValue = "${basedir}/src/main/templates")
  private File templateDir;

  /**
   * Source path with role definitions.
   */
  @Parameter(defaultValue = "${basedir}/src/main/roles")
  private File roleDir;

  /**
   * Target path for the generated configuration files.
   */
  @Parameter(defaultValue = "${project.build.directory}/configuration")
  private File target;

  /**
   * Source path with environment definitions.
   */
  @Parameter(defaultValue = "${basedir}/src/main/environments")
  private File environmentDir;

  /**
   * Target path for the prepared definition files.
   */
  @Parameter(defaultValue = "${project.build.directory}/definitions")
  private String definitionTarget;

  /**
   * List for plugins for exporting model data for nodes.
   * You can specify multiple plugins separated by ",".
   * To disable export of model data set to "none".
   */
  @Parameter(defaultValue = "yaml")
  private String modelExportNode;

  /**
   * Configuration for value providers.
   * <p>
   * This uses the same syntax as OSGi manifest headers - example:
   * </p>
   *
   * <pre>
   * valueProviderPluginName1;param1=value1;param2=value2,
   * valueProviderPluginName2;param3=value3
   * </pre>
   * <p>
   * If you want to define multiple value providers of the same type, you can use an arbitrary value provider name, and
   * specify the plugin name with the optional <code>_plugin_</code> parameter - example:
   * </p>
   *
   * <pre>
   * valueProvider1;_plugin_=valueProviderPluginName1,param1=value1;param2=value2,
   * valueProvider2;_plugin_=valueProviderPluginName1,param3=value3
   * </pre>
   */
  @Parameter
  private String valueProvider;

  /**
   * Plugin-specific configuration. This holds configuration for CONGA plugins that are not part of the built-in set of
   * CONGA plugins (e.g. configuration for the CONGA AEM Plugin).
   * <p>
   * This uses the same syntax as OSGi manifest headers - example:
   * </p>
   *
   * <pre>
   * pluginName1;param1=value1;param2=value2,
   * pluginName2;param3=value3
   * </pre>
   */
  @Parameter
  private String pluginConfig;

  /**
   * Allows to define custom artifact type to extension mappings for resolving dependencies from artifact coordinates
   * where it is not fully clear if the an extension is really the extension or a artifact type identifier.
   * Defaults to <code>bundle</code>-&gt;<code>jar</code>, <code>content-package</code>-&gt;<code>zip</code>.
   */
  @Parameter
  private Map<String,String> artifactTypeMappings;

  @Parameter(property = "project", required = true, readonly = true)
  private MavenProject project;

  @Component(role = Archiver.class, hint = "jar")
  private JarArchiver jarArchiver;

  /**
   * The archive configuration to use.
   * See <a href="http://maven.apache.org/shared/maven-archiver/index.html">Maven Archiver Reference</a>.
   */
  @Parameter
  @SuppressWarnings("PMD.ImmutableField")
  private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

  @Parameter(property = "session", readonly = true, required = true)
  private MavenSession mavenSession;

  @Component
  protected MavenProjectHelper projectHelper;

  private static final Map<String, String> DEFAULT_ARTIFACT_TYPE_MAPPINGS = Map.of(
      "bundle", "jar",
      "content-package", "zip");

  protected File getTemplateDir() {
    return templateDir;
  }

  protected File getRoleDir() {
    return roleDir;
  }

  protected File getEnvironmentDir() {
    return environmentDir;
  }

  protected File getTargetDir() {
    return target;
  }

  protected MavenProject getProject() {
    return project;
  }

  protected ModelExport getModelExport() {
    ModelExport modelExport = new ModelExport();

    String[] nodeExportPlugins = StringUtils.split(this.modelExportNode, ",");
    if (nodeExportPlugins != null) {
      modelExport.setNode(Arrays.asList(nodeExportPlugins));
    }

    return modelExport;
  }

  protected Map<String, Map<String, Object>> getValueProviderConfig() {
    return PluginConfigUtil.getConfigMap(this.valueProvider);
  }

  protected Map<String, Map<String, Object>> getPluginConfig() {
    return PluginConfigUtil.getConfigMap(this.pluginConfig);
  }

  protected Map<String, String> getArtifactTypeMappings() {
    Map<String, String> mappings = this.artifactTypeMappings;
    if (mappings == null) {
      mappings = DEFAULT_ARTIFACT_TYPE_MAPPINGS;
    }
    return mappings;
  }

  /**
   * Builds a JAR file with all CONGA definitions and resources. This is the main output artefact.
   * @throws MojoExecutionException MOJO execution exception
   * @throws MojoFailureException MOJO failure exception
   */
  protected void buildDefinitionsJarFile() throws MojoExecutionException, MojoFailureException {

    // copy definitions to classes dir
    File definitionDir = copyDefinitions();

    // build JAR artifact
    File outputFile = buildJarFile(definitionDir);

    // set or attach JAR artifact
    if (StringUtils.equalsAny(project.getPackaging(), PACKAGING_DEFINITION, PACKAGING_CONFIGURATION)) {
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
   */
  private File buildJarFile(File contentDirectory) throws MojoExecutionException {
    File jarFile = new File(project.getBuild().getDirectory(), buildJarFileName());

    MavenArchiver archiver = new MavenArchiver();
    archiver.setArchiver(jarArchiver);
    archiver.setOutputFile(jarFile);
    archive.setForced(true);

    // include definitions
    archiver.getArchiver().addDirectory(contentDirectory);

    // include resources
    for (org.apache.maven.model.Resource resource : project.getResources()) {
      File resourceDir = new File(resource.getDirectory());
      if (resourceDir.exists()) {
        archiver.getArchiver().addDirectory(resourceDir,
            toArray(resource.getIncludes()), toArray(resource.getExcludes()));
      }
    }

    try {
      archiver.createArchive(mavenSession, project, archive);
    }
    catch (ArchiverException | ManifestException | IOException | DependencyResolutionRequiredException ex) {
      throw new MojoExecutionException("Unable to build file " + jarFile.getPath() + ": " + ex.getMessage(), ex);
    }

    return jarFile;
  }

  private String[] toArray(List<String> values) {
    if (values == null || values.isEmpty()) {
      return null;
    }
    return values.toArray(new String[0]);
  }

  private String buildJarFileName() {
    StringBuilder sb = new StringBuilder();
    sb.append(project.getBuild().getFinalName());
    if (!StringUtils.equalsAny(project.getPackaging(), PACKAGING_DEFINITION, PACKAGING_CONFIGURATION)) {
      sb.append("-").append(CLASSIFIER_DEFINITION);
    }
    sb.append(".").append(FILE_EXTENSION_DEFINITION);
    return sb.toString();
  }

  /**
   * Copy definitions and template files to classes folder to include them in JAR artifact.
   */
  @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
  private File copyDefinitions() throws MojoExecutionException {
    File outputDir = new File(definitionTarget);
    if (!outputDir.exists()) {
      outputDir.mkdirs();
    }

    ResourceLoader resourceLoader = new ResourceLoader();
    ResourceCollection roleDirCol = resourceLoader.getResourceCollection(ResourceLoader.FILE_PREFIX + getRoleDir());
    ResourceCollection templateDirCol = resourceLoader.getResourceCollection(ResourceLoader.FILE_PREFIX + getTemplateDir());
    ResourceCollection environmentDirCol = resourceLoader.getResourceCollection(ResourceLoader.FILE_PREFIX + getEnvironmentDir());

    // copy definitions
    try {
      copyDefinitions(roleDirCol, outputDir, outputDir, CLASSPATH_ROLES_DIR);
      copyDefinitions(templateDirCol, outputDir, outputDir, CLASSPATH_TEMPLATES_DIR);
      copyDefinitions(environmentDirCol, outputDir, outputDir, CLASSPATH_ENVIRONMENTS_DIR);
    }
    catch (IOException ex) {
      throw new MojoExecutionException("Unable to copy definitions:" + ex.getMessage(), ex);
    }

    return outputDir;
  }

  @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
  private void copyDefinitions(ResourceCollection sourceDir, File rootOutputDir, File parentTargetDir, String dirName) throws IOException {
    if (!sourceDir.exists()) {
      return;
    }
    SortedSet<Resource> files = sourceDir.getResources();
    SortedSet<ResourceCollection> dirs = sourceDir.getResourceCollections();
    if (files.isEmpty() && dirs.isEmpty()) {
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
        Files.delete(targetFile.toPath());
      }
      try (InputStream is = file.getInputStream()) {
        byte[] data = IOUtils.toByteArray(is);
        FileUtils.writeByteArrayToFile(targetFile, data);
      }
    }

    for (ResourceCollection dir : dirs) {
      copyDefinitions(dir, rootOutputDir, targetDir, dir.getName());
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

}
