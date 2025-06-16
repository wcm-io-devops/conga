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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.artifact.resolver.ResolutionErrorHandler;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.sonatype.plexus.build.incremental.BuildContext;

import io.wcm.devops.conga.generator.Generator;
import io.wcm.devops.conga.generator.GeneratorOptions;
import io.wcm.devops.conga.generator.spi.context.PluginContextOptions;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;
import io.wcm.devops.conga.tooling.maven.plugin.util.ClassLoaderUtil;
import io.wcm.devops.conga.tooling.maven.plugin.util.MavenContext;
import io.wcm.devops.conga.tooling.maven.plugin.util.VersionInfoUtil;

/**
 * Generates configuration using CONGA generator.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresProject = true, threadSafe = true,
    requiresDependencyResolution = ResolutionScope.COMPILE)
public class GenerateMojo extends AbstractCongaMojo {

  /**
   * Selected environments to generate.
   */
  @Parameter(property = "conga.environments")
  private String[] environments;

  /**
   * Selected nodes to generate.
   */
  @Parameter(property = "conga.nodes")
  private String[] nodes;

  /**
   * Delete folders of environments before generating the new files.
   */
  @Parameter(defaultValue = "false")
  private boolean deleteBeforeGenerate;

  /**
   * Is it allowed to create symlinks instead of copying files if they are local files e.g. from Maven Repository.
   */
  @Parameter(defaultValue = "true")
  private boolean allowSymlinks;

  /**
   * Plugin keys (groupId:artifactId) of additional Maven plugins of the current project's POM
   * to be included in the model export version information.
   */
  @Parameter(defaultValue = "io.wcm.maven.plugins:wcmio-content-package-maven-plugin")
  private String[] versionInfoAdditionalPlugins;

  @Parameter(property = "project", required = true, readonly = true)
  private MavenProject project;

  @Inject
  private org.apache.maven.repository.RepositorySystem repositorySystem;
  @Inject
  private ResolutionErrorHandler resolutionErrorHandler;
  @Inject
  private RepositorySystem repoSystem;
  @Inject
  private BuildContext buildContext;
  @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
  private RepositorySystemSession repoSession;
  @Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
  private List<RemoteRepository> remoteRepos;
  @Parameter(defaultValue = "${session}", readonly = true, required = false)
  private MavenSession session;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {

    MavenContext mavenContext = new MavenContext()
        .project(project)
        .session(session)
        .setRepositorySystem(repositorySystem)
        .resolutionErrorHandler(resolutionErrorHandler)
        .buildContext(buildContext)
        .log(getLog())
        .repoSystem(repoSystem)
        .repoSession(repoSession)
        .remoteRepos(remoteRepos)
        .artifactTypeMappings(getArtifactTypeMappings());

    PluginManager pluginManager = new PluginManagerImpl();

    PluginContextOptions pluginContextOptions = new PluginContextOptions()
        .pluginManager(pluginManager)
        .valueProviderConfig(getValueProviderConfig())
        .genericPluginConfig(getPluginConfig())
        .containerContext(mavenContext)
        .logger(new MavenSlf4jLogFacade(getLog()));

    GeneratorOptions options = new GeneratorOptions()
        .baseDir(project.getBasedir())
        .roleDir(getRoleDir())
        .templateDir(getTemplateDir())
        .environmentDir(getEnvironmentDir())
        .destDir(getTargetDir())
        .deleteBeforeGenerate(deleteBeforeGenerate)
        .version(project.getVersion())
        .setAllowSymlinks(allowSymlinks)
        .modelExport(getModelExport())
        .valueProviderConfig(getValueProviderConfig())
        .genericPluginConfig(getPluginConfig())
        .containerContext(mavenContext)
        .containerClasspathUrls(ClassLoaderUtil.getMavenProjectClasspathUrls(project))
        .pluginManager(pluginManager)
        .dependencyVersionBuilder(new DependencyVersionBuilder(pluginContextOptions))
        .containerVersionInfo(buildContainerVersionInfo())
        .logger(new MavenSlf4jLogFacade(getLog()));

    Generator generator = new Generator(options);
    generator.generate(environments, nodes);
  }

  /**
   * Build version information about CONGA Maven plugin and CONGA plugins, plus
   * additional plugin versions as defined in plugin configuration. This version
   * information is included in the model export.
   * @return Version information
   */
  private Map<String, String> buildContainerVersionInfo() {
    Map<String, String> versionInfo = new HashMap<>();

    Properties pluginProps = VersionInfoUtil.getVersionInfoProperties(project);
    for (Map.Entry<Object, Object> entry : pluginProps.entrySet()) {
      versionInfo.put(entry.getKey().toString(), entry.getValue().toString());
    }

    for (String pluginKey : versionInfoAdditionalPlugins) {
      String pluginVersion = VersionInfoUtil.getPluginVersionFromPluginManagement(pluginKey, project);
      if (pluginVersion != null) {
        versionInfo.put(pluginKey, pluginVersion);
      }
    }

    return versionInfo;
  }

}
