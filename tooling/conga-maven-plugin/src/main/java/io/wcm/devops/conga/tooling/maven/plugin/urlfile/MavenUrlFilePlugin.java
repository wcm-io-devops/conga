/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2016 wcm.io
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
package io.wcm.devops.conga.tooling.maven.plugin.urlfile;

import static org.apache.maven.artifact.Artifact.SCOPE_COMPILE;
import static org.apache.maven.artifact.Artifact.SCOPE_RUNTIME;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.ArtifactType;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

import io.wcm.devops.conga.generator.spi.UrlFilePlugin;
import io.wcm.devops.conga.generator.spi.context.UrlFilePluginContext;

/**
 * Download files from Maven artifact repository.
 */
public class MavenUrlFilePlugin implements UrlFilePlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "maven";

  /**
   * Url prefix
   */
  public static final String PREFIX = "mvn:";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean accepts(String url, UrlFilePluginContext context) {
    return StringUtils.startsWith(url, PREFIX);
  }

  @Override
  public String getFileName(String url, UrlFilePluginContext context) throws IOException {
    String mavenCoords = StringUtils.substringAfter(url, PREFIX);
    MavenUrlFilePluginContext mavenContext = (MavenUrlFilePluginContext)context.getContainerContext();
    File file = getArtifact(mavenCoords, context, mavenContext).getFile();
    return file.getName();
  }

  @Override
  public InputStream getFile(String url, UrlFilePluginContext context) throws IOException {
    String mavenCoords = StringUtils.substringAfter(url, PREFIX);
    MavenUrlFilePluginContext mavenContext = (MavenUrlFilePluginContext)context.getContainerContext();
    File file = getArtifact(mavenCoords, context, mavenContext).getFile();
    return new BufferedInputStream(new FileInputStream(file));
  }

  @Override
  public URL getFileUrl(String url, UrlFilePluginContext context) throws IOException {
    String mavenCoords = StringUtils.substringAfter(url, PREFIX);
    MavenUrlFilePluginContext mavenContext = (MavenUrlFilePluginContext)context.getContainerContext();
    File file = getArtifact(mavenCoords, context, mavenContext).getFile();
    return file.toURI().toURL();
  }

  @Override
  public List<URL> getFileUrlsWithDependencies(String url, UrlFilePluginContext context) throws IOException {
    String mavenCoords = StringUtils.substringAfter(url, PREFIX);
    MavenUrlFilePluginContext mavenContext = (MavenUrlFilePluginContext)context.getContainerContext();
    List<URL> urls = new ArrayList<>();
    Artifact artifact = getArtifact(mavenCoords, context, mavenContext);
    urls.add(artifact.getFile().toURI().toURL());

    // get transitive dependencies of artifact
    for (Artifact dependencyArtifact : getTransitiveDependencies(artifact, context, mavenContext)) {
      urls.add(dependencyArtifact.getFile().toURI().toURL());
    }

    return urls;
  }

  private List<Artifact> getTransitiveDependencies(Artifact artifact,
      UrlFilePluginContext context, MavenUrlFilePluginContext mavenContext) throws IOException {
    List<Artifact> dependencies = new ArrayList<>();
    ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
    descriptorRequest.setArtifact(artifact);
    descriptorRequest.setRepositories(mavenContext.getRemoteRepos());
    try {
      ArtifactDescriptorResult result = mavenContext.getRepoSystem().readArtifactDescriptor(mavenContext.getRepoSession(), descriptorRequest);
      for (Dependency dependency : result.getDependencies()) {
        if (StringUtils.equalsAny(dependency.getScope(), SCOPE_COMPILE, SCOPE_RUNTIME)) {
          Artifact resolvedArtifact = resolveArtifact(dependency.getArtifact(), context, mavenContext);
          dependencies.add(resolvedArtifact);
        }
      }
    }
    catch (ArtifactDescriptorException ex) {
      throw new IOException("Unable to get Maven artifact descriptor for '" + artifact + "': " + ex.getMessage(), ex);
    }
    return dependencies;
  }

  private Artifact getArtifact(String artifact, UrlFilePluginContext context, MavenUrlFilePluginContext mavenContext) throws IOException {

    Artifact artifactObject;
    if (StringUtils.contains(artifact, "/")) {
      artifactObject = getArtifactFromMavenCoordinatesSlingStartStyle(artifact, context, mavenContext);
    }
    else {
      artifactObject = getArtifactFromMavenCoordinates(artifact, context, mavenContext);
    }

    return resolveArtifact(artifactObject, context, mavenContext);
  }

  private Artifact resolveArtifact(Artifact artifact, UrlFilePluginContext context, MavenUrlFilePluginContext mavenContext)
      throws IOException {
    ArtifactRequest artifactRequest = new ArtifactRequest();
    artifactRequest.setArtifact(artifact);
    artifactRequest.setRepositories(mavenContext.getRemoteRepos());
    try {
      ArtifactResult result = mavenContext.getRepoSystem().resolveArtifact(mavenContext.getRepoSession(), artifactRequest);
      return result.getArtifact();
    }
    catch (final ArtifactResolutionException ex) {
      throw new IOException("Unable to get artifact for " + artifact, ex);
    }
  }

  /**
   * Parse coordinates following definition from https://maven.apache.org/pom.html#Maven_Coordinates
   * @param artifact Artifact coordinates
   * @return Artifact object
   */
  private Artifact getArtifactFromMavenCoordinates(String artifact,
      UrlFilePluginContext context, MavenUrlFilePluginContext mavenContext) throws IOException {
    String[] parts = StringUtils.splitPreserveAllTokens(artifact, ":");

    String version = null;
    String packaging = null;
    String classifier = null;

    switch (parts.length) {
      case 2:
        // groupId:artifactId
        break;

      case 3:
        // groupId:artifactId:version
        version = StringUtils.defaultIfBlank(parts[2], null);
        break;

      case 4:
        // groupId:artifactId:packaging:version
        packaging = StringUtils.defaultIfBlank(parts[2], null);
        version = StringUtils.defaultIfBlank(parts[3], null);
        break;

      case 5:
        // groupId:artifactId:packaging:classifier:version
        packaging = StringUtils.defaultIfBlank(parts[2], null);
        classifier = StringUtils.defaultIfBlank(parts[3], null);
        version = StringUtils.defaultIfBlank(parts[4], null);
        break;

      default:
        throw new IOException("Invalid artifact: " + artifact);
    }

    String groupId = StringUtils.defaultIfBlank(parts[0], null);
    String artifactId = StringUtils.defaultIfBlank(parts[1], null);

    return createArtifact(artifactId, groupId, packaging, classifier, version, context, mavenContext);
  }

  /**
   * Parse coordinates in slingstart/Pax URL style following definition from https://ops4j1.jira.com/wiki/x/CoA6
   * @param artifact Artifact coordinates
   * @return Artifact object
   */
  private Artifact getArtifactFromMavenCoordinatesSlingStartStyle(String artifact,
      UrlFilePluginContext context, MavenUrlFilePluginContext mavenContext) throws IOException {
    String[] parts = StringUtils.splitPreserveAllTokens(artifact, "/");

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
        throw new IOException("Invalid artifact: " + artifact);
    }

    String groupId = StringUtils.defaultIfBlank(parts[0], null);
    String artifactId = StringUtils.defaultIfBlank(parts[1], null);

    return createArtifact(artifactId, groupId, packaging, classifier, version, context, mavenContext);
  }

  private Artifact createArtifact(String artifactId, String groupId, String packaging, String classifier, String version,
      UrlFilePluginContext context, MavenUrlFilePluginContext mavenContext) throws IOException {

    String artifactVersion = version;
    if (artifactVersion == null) {
      artifactVersion = resolveArtifactVersion(artifactId, groupId, packaging, classifier, context, mavenContext);
    }
    String artifactPackaging = packaging;
    if (artifactPackaging == null) {
      artifactPackaging = "jar";
    }

    if (StringUtils.isBlank(groupId) || StringUtils.isBlank(artifactId) || StringUtils.isBlank(artifactVersion)) {
      throw new IOException("Invalid Maven artifact reference: "
          + "artifactId=" + artifactId + ", "
          + "groupId=" + groupId + ", "
          + "version=" + artifactVersion + ", "
          + "packaging=" + artifactPackaging + ", "
          + "classifier=" + classifier);
    }

    String extension = packaging;
    ArtifactType artifactType = mavenContext.getRepoSession().getArtifactTypeRegistry().get(artifactPackaging);
    if (artifactType != null) {
      extension = artifactType.getExtension();
    }
    return new DefaultArtifact(groupId, artifactId, classifier, extension, artifactVersion, artifactType);
  }

  private String resolveArtifactVersion(String artifactId, String groupId, String packaging, String classifier,
      UrlFilePluginContext context, MavenUrlFilePluginContext mavenContext) throws IOException {
    String version = findVersionInMavenProject(artifactId, groupId, packaging, classifier, mavenContext.getProject());
    if (version == null) {
      version = findVersionInEnvironmentDependencies(artifactId, groupId, packaging, classifier, context, mavenContext);
    }
    return version;
  }

  private String findVersionInMavenProject(String artifactId, String groupId, String packaging, String classifier, MavenProject mavenProject) {
    Set<org.apache.maven.artifact.Artifact> dependencies = mavenProject.getArtifacts();
    if (dependencies != null) {
      for (org.apache.maven.artifact.Artifact dependency : dependencies) {
        if (artifactEquals(dependency, artifactId, groupId, packaging, classifier)) {
          return dependency.getVersion();
        }
      }
    }
    return null;
  }

  private boolean artifactEquals(org.apache.maven.artifact.Artifact dependency, String artifactId, String groupId, String packaging, String classifier) {
    return StringUtils.equals(dependency.getGroupId(), groupId)
        && StringUtils.equals(dependency.getArtifactId(), artifactId)
        && StringUtils.equals(dependency.getClassifier(), classifier)
        && StringUtils.equals(dependency.getType(), packaging);
  }

  private String findVersionInEnvironmentDependencies(String artifactId, String groupId, String packaging, String classifier,
      UrlFilePluginContext context, MavenUrlFilePluginContext mavenContext) throws IOException {

    if (context.getEnvironment() == null) {
      return null;
    }

    List<Artifact> dependencies = new ArrayList<>();
    for (String dependencyUrl : context.getEnvironment().getDependencies()) {
      if (!StringUtils.startsWith(dependencyUrl, PREFIX)) {
        continue;
      }

      String mavenCoords = StringUtils.substringAfter(dependencyUrl, PREFIX);
      Artifact artifact = getArtifact(mavenCoords, context, mavenContext);
      dependencies.add(artifact);
      dependencies.addAll(getTransitiveDependencies(artifact, context, mavenContext));
    }

    for (Artifact dependency : dependencies) {
      if (artifactEquals(dependency, artifactId, groupId, packaging, classifier)) {
        return dependency.getVersion();
      }
    }

    return null;
  }

  private boolean artifactEquals(Artifact dependency, String artifactId, String groupId, String packaging, String classifier) {
    return StringUtils.equals(dependency.getGroupId(), groupId)
        && StringUtils.equals(dependency.getArtifactId(), artifactId)
        && StringUtils.equals(StringUtils.defaultString(dependency.getClassifier()), StringUtils.defaultString(classifier))
        && StringUtils.equals(dependency.getExtension(), packaging);
  }

}
