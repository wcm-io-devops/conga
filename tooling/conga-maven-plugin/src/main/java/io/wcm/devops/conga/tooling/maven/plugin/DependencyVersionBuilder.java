/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2018 wcm.io
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.eclipse.aether.artifact.Artifact;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.GeneratorOptions;
import io.wcm.devops.conga.generator.util.FileUtil;
import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.tooling.maven.plugin.util.MavenArtifactHelper;
import io.wcm.devops.conga.tooling.maven.plugin.util.MavenContext;

class DependencyVersionBuilder implements Function<Environment, Collection<String>> {

  private final MavenContext mavenContext;

  DependencyVersionBuilder(MavenContext mavenContext) {
    this.mavenContext = mavenContext;
  }

  @Override
  public Collection<String> apply(Environment environment) {
    MavenArtifactHelper mavenArtifactHelper = new MavenArtifactHelper(mavenContext, environment);

    try {
      List<Artifact> dependencyArtifacts = new ArrayList<>();
      dependencyArtifacts.addAll(getCompileDependencyArtifacts(mavenArtifactHelper));
      dependencyArtifacts.addAll(getEnvironmentDependencyArtifacts(environment, mavenArtifactHelper));

      return new TreeSet<>(
          dependencyArtifacts.stream()
              // include only dependencies with a CONGA-INF/ directory
              .filter(this::hasCongaDefinitions)
              // transform to string
              .map(this::toArtifactCoordsPaxUrlStyle)
              .collect(Collectors.toSet()));
    }
    catch (IOException ex) {
      throw new GeneratorException(ex.getMessage(), ex);
    }
  }

  @SuppressWarnings("deprecation")
  private List<Artifact> getCompileDependencyArtifacts(MavenArtifactHelper mavenArtifactHelper) throws IOException {
    List<Artifact> artifacts = new ArrayList<>();
    for (Dependency dependency : mavenContext.getProject().getCompileDependencies()) {
      artifacts.add(mavenArtifactHelper.resolveArtifact(
          dependency.getGroupId(),
          dependency.getArtifactId(),
          dependency.getType(),
          dependency.getClassifier(),
          dependency.getVersion()));
    }
    return artifacts;
  }

  private List<Artifact> getEnvironmentDependencyArtifacts(Environment environment, MavenArtifactHelper mavenArtifactHelper) throws IOException {
    return mavenArtifactHelper.dependencyUrlsToArtifactsWithTransitiveDependencies(environment.getDependencies());
  }

  /**
   * Checks if the JAR file of the given dependency has a CONGA-INF/ directory.
   * @param artifact Dependency
   * @return true if configuration definitions found
   */
  private boolean hasCongaDefinitions(Artifact artifact) {
    if (!StringUtils.equalsAny(artifact.getExtension(), "jar")) {
      return false;
    }
    String fileInfo = artifact.toString();
    try {
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
   * Build artifact coords string.
   * @param artifact Artifact
   * @return Artifact coords
   */
  private String toArtifactCoordsPaxUrlStyle(Artifact artifact) {
    return artifact.getGroupId() + "/" + artifact.getArtifactId()
        + "/" + artifact.getVersion()
        + (StringUtils.isNotEmpty(artifact.getClassifier()) ? "/" + artifact.getExtension() + "/" + artifact.getClassifier() : "");
  }

}
