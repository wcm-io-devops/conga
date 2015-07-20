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

import io.wcm.devops.conga.generator.util.FileUtil;
import io.wcm.devops.conga.resource.ResourceLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Generates a file with information CONGA maven plugin version and CONGA plugins versions used to generate this
 * artifact.
 */
@Mojo(name = "generate-version-info", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresProject = true, threadSafe = true)
public class GenerateVersionInfoMojo extends AbstractCongaMojo {

  /**
   * Target path for the prepared definition files.
   */
  @Parameter(defaultValue = "${project.build.directory}/definitions")
  private String definitionTarget;

  @Parameter(property = "project", required = true, readonly = true)
  private MavenProject project;

  private ResourceLoader resourceLoader;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    resourceLoader = new ResourceLoader();

    File outputDir = new File(definitionTarget, BuildConstants.CLASSPATH_PREFIX);
    if (!outputDir.exists()) {
      outputDir.mkdirs();
    }

    File propsFile = new File(outputDir, BuildConstants.FILE_VERSION_INFO);
    if (propsFile.exists()) {
      propsFile.delete();
    }
    Properties props = getVersionInfoProperties();
    try (OutputStream os = new FileOutputStream(propsFile)) {
      props.store(os, "CONGA Version Info");
    }
    catch (IOException ex) {
      throw new MojoExecutionException("Error generating version info to "
          + FileUtil.getCanonicalPath(propsFile) + ": " + ex.getMessage(), ex);
    }
  }

  /**
   * Detects versions from maven-conga-plugin and additional CONGA plugins used.
   * @return Properties with versions
   */
  private Properties getVersionInfoProperties() {
    Properties props = new Properties();

    // add version info about the conga-maven-plugin itself, and direct plugin dependencies (assumed conga plugins)
    Plugin congaPlugin = project.getPlugin(BuildConstants.CONGA_MAVEN_PLUGIN_KEY);
    if (congaPlugin != null) {
      props.put(congaPlugin.getKey(), congaPlugin.getVersion());

      congaPlugin.getDependencies().stream()
      .forEach(dependency -> props.put(Plugin.constructKey(dependency.getGroupId(), dependency.getArtifactId()), dependency.getVersion()));
    }

    return props;
  }

  @Override
  protected ResourceLoader getResourceLoader() {
    return resourceLoader;
  }

}
