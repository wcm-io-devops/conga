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
package io.wcm.devops.conga.tooling.maven.plugin.util;

import static org.apache.maven.artifact.Artifact.SCOPE_COMPILE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.ArtifactType;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

import com.google.common.collect.ImmutableList;

import io.wcm.devops.conga.generator.spi.context.PluginContextOptions;
import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.tooling.maven.plugin.urlfile.MavenUrlFilePlugin;

/**
 * Helper for resolving maven artifacts.
 */
public class MavenArtifactHelper {

  private final MavenProject project;
  private final RepositorySystem repoSystem;
  private final RepositorySystemSession repoSession;
  private final List<RemoteRepository> remoteRepos;
  private final Map<String, String> artifactTypeMappings;
  private final List<String> environmentDependencyUrls;
  private final PluginContextOptions pluginContextOptions;

  /**
   * @param environment CONGA environment
   * @param pluginContextOptions Plugin context options
   */
  public MavenArtifactHelper(Environment environment, PluginContextOptions pluginContextOptions) {
    MavenContext mavenContext = (MavenContext)pluginContextOptions.getContainerContext();
    this.project = mavenContext.getProject();
    this.repoSystem = mavenContext.getRepoSystem();
    this.repoSession = mavenContext.getRepoSession();
    this.remoteRepos = mavenContext.getRemoteRepos();
    this.artifactTypeMappings = mavenContext.getArtifactTypeMappings();
    this.environmentDependencyUrls = environment != null ? environment.getDependencies() : ImmutableList.of();
    this.pluginContextOptions = pluginContextOptions;
  }

  /**
   * Get Maven artifact for given artifact coordinates.
   * @param artifactCoords Artifact coordinates in either Maven-style or Pax URL-style.
   * @return Maven artifact
   * @throws IOException If artifact resolution was not successful
   */
  public Artifact resolveArtifact(String artifactCoords) throws IOException {
    Artifact artifact;
    if (StringUtils.contains(artifactCoords, "/")) {
      artifact = getArtifactFromMavenCoordinatesPaxUrlStyle(artifactCoords);
    }
    else {
      artifact = getArtifactFromMavenCoordinates(artifactCoords);
    }
    return resolveArtifact(artifact);
  }

  /**
   * Get Maven artifact for given artifact coordinates.
   * @param groupId Group Id
   * @param artifactId Artifact Id
   * @param type Type
   * @param classifier Classifier
   * @param version Version
   * @return Artifact
   * @throws IOException If dependency resolution fails
   */
  public Artifact resolveArtifact(String groupId, String artifactId, String type, String classifier, String version) throws IOException {
    Artifact artifact = createArtifact(groupId, artifactId, type, classifier, version);
    return resolveArtifact(artifact);
  }

  /**
   * Get transitive compile dependencies of given artifact.
   * @param artifact Maven artifact
   * @return List of artifact dependencies
   * @throws IOException If artifact resolution was not successful
   */
  public List<Artifact> getTransitiveDependencies(Artifact artifact) throws IOException {
    List<Artifact> dependencies = new ArrayList<>();
    ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
    descriptorRequest.setArtifact(artifact);
    descriptorRequest.setRepositories(remoteRepos);
    try {
      ArtifactDescriptorResult result = repoSystem.readArtifactDescriptor(repoSession, descriptorRequest);
      for (Dependency dependency : result.getDependencies()) {
        if (StringUtils.equals(dependency.getScope(), SCOPE_COMPILE)) {
          Artifact resolvedArtifact = resolveArtifact(dependency.getArtifact());
          dependencies.add(resolvedArtifact);
        }
      }
    }
    catch (ArtifactDescriptorException ex) {
      throw new IOException("Unable to get artifact descriptor for: '" + artifact + "': " + ex.getMessage(), ex);
    }
    return dependencies;
  }

  /**
   * Transform a list of dependency urls to a list of references maven artifacts including their transitive
   * dependencies.
   * @param dependencyUrls Dependency URLs
   * @return List of artifacts
   * @throws IOException If dependency resolution fails
   */
  public List<Artifact> dependencyUrlsToArtifactsWithTransitiveDependencies(Collection<String> dependencyUrls) throws IOException {
    List<Artifact> artifacts = new ArrayList<>();
    for (String dependencyUrl : environmentDependencyUrls) {
      String resolvedDependencyUrl = ClassLoaderUtil.resolveDependencyUrl(dependencyUrl, pluginContextOptions);
      if (!StringUtils.startsWith(resolvedDependencyUrl, MavenUrlFilePlugin.PREFIX)) {
        continue;
      }

      String mavenCoords = MavenUrlFilePlugin.getMavenCoords(resolvedDependencyUrl);
      Artifact artifact = resolveArtifact(mavenCoords);
      artifacts.add(artifact);
      artifacts.addAll(getTransitiveDependencies(artifact));
    }
    return artifacts;
  }

  /**
   * Parse coordinates following definition from https://maven.apache.org/pom.html#Maven_Coordinates
   * @param artifactCoords Artifact coordinates
   * @return Artifact object
   */
  private Artifact getArtifactFromMavenCoordinates(String artifactCoords) throws IOException {
    try {
      Artifact artifact = new DefaultArtifact(artifactCoords);
      return createArtifact(artifact.getGroupId(), artifact.getArtifactId(), artifact.getExtension(), artifact.getClassifier(), artifact.getVersion());
    }
    catch (IllegalArgumentException ex) {
      throw new IOException(ex.getMessage());
    }
  }

  /**
   * Parse coordinates in slingstart/Pax URL style following definition from https://ops4j1.jira.com/wiki/x/CoA6
   * @param artifactCoords Artifact coordinates
   * @return Artifact object
   */
  private Artifact getArtifactFromMavenCoordinatesPaxUrlStyle(String artifactCoords) throws IOException {
    String[] parts = StringUtils.splitPreserveAllTokens(artifactCoords, "/");

    String version = null;
    String packaging = null;
    String classifier = null;

    switch (parts.length) {
      case 2:
        // groupId/artifactId
        break;

      case 3:
        // groupId/artifactId/version
        version = StringUtils.defaultIfBlank(parts[2], null);
        break;

      case 4:
        // groupId/artifactId/version/type
        packaging = StringUtils.defaultIfBlank(parts[3], null);
        version = StringUtils.defaultIfBlank(parts[2], null);
        break;

      case 5:
        // groupId/artifactId/version/type/classifier
        packaging = StringUtils.defaultIfBlank(parts[3], null);
        classifier = StringUtils.defaultIfBlank(parts[4], null);
        version = StringUtils.defaultIfBlank(parts[2], null);
        break;

      default:
        throw new IOException("Invalid artifact: " + artifactCoords);
    }

    String groupId = StringUtils.defaultIfBlank(parts[0], null);
    String artifactId = StringUtils.defaultIfBlank(parts[1], null);

    return createArtifact(groupId, artifactId, packaging, classifier, version);
  }

  private Artifact createArtifact(String groupId, String artifactId, String type, String classifier, String version) throws IOException {

    String artifactVersion = version;
    if (artifactVersion == null) {
      artifactVersion = resolveArtifactVersion(groupId, artifactId, type, classifier);
    }
    String artifactExtension = type;
    if (artifactExtension == null) {
      artifactExtension = "jar";
    }

    ArtifactType artifactType = repoSession.getArtifactTypeRegistry().get(artifactExtension);
    if (artifactType != null) {
      artifactExtension = artifactType.getExtension();
    }

    // apply custom mapping from artifact type to extension if defined in plugin config
    if (artifactTypeMappings != null && artifactTypeMappings.containsKey(artifactExtension)) {
      artifactExtension = artifactTypeMappings.get(artifactExtension);
    }

    if (StringUtils.isBlank(groupId) || StringUtils.isBlank(artifactId) || StringUtils.isBlank(artifactVersion)) {
      throw new IOException("Invalid Maven artifact reference: "
          + "artifactId=" + artifactId + ", "
          + "groupId=" + groupId + ", "
          + "version=" + artifactVersion + ", "
          + "extension=" + artifactExtension + ", "
          + "classifier=" + classifier + ","
          + "type=" + artifactType);
    }

    return new DefaultArtifact(groupId, artifactId, classifier, artifactExtension, artifactVersion, artifactType);
  }

  private Artifact resolveArtifact(Artifact artifact) throws IOException {
    ArtifactRequest artifactRequest = new ArtifactRequest();
    artifactRequest.setArtifact(artifact);
    artifactRequest.setRepositories(remoteRepos);
    try {
      ArtifactResult result = repoSystem.resolveArtifact(repoSession, artifactRequest);
      return result.getArtifact();
    }
    catch (final ArtifactResolutionException ex) {
      throw new IOException("Unable to get artifact for '" + artifact + "': " + ex.getMessage(), ex);
    }
  }

  private String resolveArtifactVersion(String groupId, String artifactId, String type, String classifier) throws IOException {
    String version = findVersionInMavenProject(groupId, artifactId, type, classifier);
    if (version == null) {
      version = findVersionInEnvironmentDependencies(groupId, artifactId, type, classifier);
    }
    return version;
  }

  private String findVersionInMavenProject(String groupId, String artifactId, String type, String classifier) {
    Set<org.apache.maven.artifact.Artifact> dependencies = project.getArtifacts();
    if (dependencies != null) {
      for (org.apache.maven.artifact.Artifact dependency : dependencies) {
        if (artifactEquals(dependency, artifactId, groupId, type, classifier)) {
          return dependency.getVersion();
        }
      }
    }
    return null;
  }

  private boolean artifactEquals(org.apache.maven.artifact.Artifact dependency, String artifactId, String groupId, String type, String classifier) {
    return StringUtils.equals(dependency.getGroupId(), groupId)
        && StringUtils.equals(dependency.getArtifactId(), artifactId)
        && StringUtils.equals(StringUtils.defaultString(dependency.getClassifier()), StringUtils.defaultString(classifier))
        && StringUtils.equals(dependency.getType(), type);
  }

  private String findVersionInEnvironmentDependencies(String groupId, String artifactId, String packaging, String classifier) throws IOException {
    List<Artifact> dependencies = dependencyUrlsToArtifactsWithTransitiveDependencies(environmentDependencyUrls);

    for (Artifact dependency : dependencies) {
      if (artifactEquals(dependency, groupId, artifactId, packaging, classifier)) {
        return dependency.getVersion();
      }
    }

    return null;
  }

  private boolean artifactEquals(Artifact dependency, String groupId, String artifactId, String type, String classifier) {
    return StringUtils.equals(dependency.getGroupId(), groupId)
        && StringUtils.equals(dependency.getArtifactId(), artifactId)
        && StringUtils.equals(StringUtils.defaultString(dependency.getClassifier()), StringUtils.defaultString(classifier))
        && StringUtils.equals(dependency.getExtension(), type);
  }

}
