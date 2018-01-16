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

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

import io.wcm.devops.conga.generator.Generator;
import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.GeneratorOptions;
import io.wcm.devops.conga.generator.util.FileUtil;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;
import io.wcm.devops.conga.tooling.maven.plugin.urlfile.MavenUrlFilePluginContext;
import io.wcm.devops.conga.tooling.maven.plugin.util.ClassLoaderUtil;

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
   * Delete folders of environments before generating the new files.
   */
  @Parameter(defaultValue = "false")
  private boolean deleteBeforeGenerate;

  @Parameter(property = "project", required = true, readonly = true)
  private MavenProject project;

  @Component
  private RepositorySystem repoSystem;
  @Parameter(property = "repositorySystemSession", readonly = true)
  private RepositorySystemSession repoSession;
  @Parameter(property = "project.remotePluginRepositories", readonly = true)
  private List<RemoteRepository> remoteRepos;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {

    MavenUrlFilePluginContext urlFilePluginContainerContext = new MavenUrlFilePluginContext()
        .project(project)
        .repoSystem(repoSystem)
        .repoSession(repoSession)
        .remoteRepos(remoteRepos);

    GeneratorOptions options = new GeneratorOptions()
        .baseDir(project.getBasedir())
        .roleDir(getRoleDir())
        .templateDir(getTemplateDir())
        .environmentDir(getEnvironmentDir())
        .destDir(getTargetDir())
        .deleteBeforeGenerate(deleteBeforeGenerate)
        .version(project.getVersion())
        .modelExport(getModelExport())
        .valueProviderConfig(getValueProviderConfig())
        .genericPluginConfig(getPluginConfig())
        .urlFilePluginContainerContext(urlFilePluginContainerContext)
        .containerClasspathUrls(ClassLoaderUtil.getMavenProjectClasspathUrls(project))
        .containerDependencyVersions(buildDependencyVersionList())
        .pluginManager(new PluginManagerImpl())
        .logger(new MavenSlf4jLogFacade(getLog()));

    Generator generator = new Generator(options);
    generator.generate(environments);
  }

  /**
   * Build list of referenced dependencies to be included in file header of generated files.
   * @return Version list
   */
  @SuppressWarnings("deprecation")
  public List<String> buildDependencyVersionList() {
    return project.getCompileDependencies().stream()
        // include only dependencies with a CONGA-INF/ directory
        .filter(this::hasCongaDefinitions)
        // transform to string
        .map(dependency -> dependency.getGroupId() + "/" + dependency.getArtifactId()
            + "/" + dependency.getVersion()
            + (dependency.getClassifier() != null ? "/" + dependency.getType() + "/" + dependency.getClassifier() : ""))
        .sorted()
        .collect(Collectors.toList());
  }

  /**
   * Checks if the JAR file of the given dependency has a CONGA-INF/ directory.
   * @param dependency Dependency
   * @return true if configuration definitions found
   */
  private boolean hasCongaDefinitions(Dependency dependency) {
    if (!StringUtils.equalsAny(dependency.getType(), "jar", "config-definition")) {
      return false;
    }
    String fileInfo = dependency.toString();
    try {
      Artifact artifact = getArtifact(dependency);
      fileInfo = FileUtil.getCanonicalPath(artifact.getFile());
      try (ZipFile zipFile = new ZipFile(artifact.getFile())) {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
          ZipEntry entry = entries.nextElement();
          if (StringUtils.startsWith(entry.getName(), GeneratorOptions.CLASSPATH_PREFIX)) {
            return true;
          }
        }
      }
    }
    catch (IOException ex) {
      throw new GeneratorException("Unable to read from JAR file: " + fileInfo, ex);
    }
    return false;
  }

  /**
   * Get a resolved Artifact from the coordinates provided
   * @return the artifact, which has been resolved.
   */
  private Artifact getArtifact(Dependency dependency) throws IOException {
    Artifact artifact = new DefaultArtifact(dependency.getGroupId(),
        dependency.getArtifactId(),
        dependency.getClassifier(),
        dependency.getType(),
        dependency.getVersion(),
        repoSession.getArtifactTypeRegistry().get(dependency.getType()));
    ArtifactRequest artifactRequest = new ArtifactRequest();
    artifactRequest.setArtifact(artifact);
    artifactRequest.setRepositories(remoteRepos);
    try {
      ArtifactResult result = repoSystem.resolveArtifact(repoSession, artifactRequest);
      return result.getArtifact();
    }
    catch (final ArtifactResolutionException ex) {
      throw new IOException("Unable to get artifact for " + dependency, ex);
    }
  }

}
