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
package io.wcm.devops.conga.tooling.maven.plugin.util;

import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.resolver.ResolutionErrorHandler;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * Contains maven-specific context objects.
 */
public final class MavenContext {

  private MavenProject project;
  private MavenSession session;
  private org.apache.maven.repository.RepositorySystem repositorySystem;
  private ResolutionErrorHandler resolutionErrorHandler;
  private BuildContext buildContext;
  private Log log;
  private RepositorySystem repoSystem;
  private RepositorySystemSession repoSession;
  private List<RemoteRepository> remoteRepos;
  private Map<String, String> artifactTypeMappings;

  /**
   * @return Maven project
   */
  public MavenProject getProject() {
    return this.project;
  }

  /**
   * @param value Maven project
   * @return this
   */
  public MavenContext project(MavenProject value) {
    this.project = value;
    return this;
  }

  /**
   * @return Maven Session
   */
  public MavenSession getSession() {
    return this.session;
  }

  /**
   * @param value Maven Session
   * @return this
   */
  public MavenContext session(MavenSession value) {
    this.session = value;
    return this;
  }

  /**
   * @return Repository system
   */
  public org.apache.maven.repository.RepositorySystem getRepositorySystem() {
    return this.repositorySystem;
  }

  /**
   * @param value Repository system
   * @return this
   */
  public MavenContext setRepositorySystem(org.apache.maven.repository.RepositorySystem value) {
    this.repositorySystem = value;
    return this;
  }

  /**
   * @return Resolution error handler
   */
  public ResolutionErrorHandler getResolutionErrorHandler() {
    return this.resolutionErrorHandler;
  }

  /**
   * @param value Resolution error handler
   * @return this
   */
  public MavenContext resolutionErrorHandler(ResolutionErrorHandler value) {
    this.resolutionErrorHandler = value;
    return this;
  }

  /**
   * @return Build context
   */
  public BuildContext getBuildContext() {
    return this.buildContext;
  }

  /**
   * @param value Build context
   * @return this
   */
  public MavenContext buildContext(BuildContext value) {
    this.buildContext = value;
    return this;
  }

  /**
   * @return Log
   */
  public Log getLog() {
    return this.log;
  }

  /**
   * @param value Log
   * @return this
   */
  public MavenContext log(Log value) {
    this.log = value;
    return this;
  }

  /**
   * @return Repository system
   */
  public RepositorySystem getRepoSystem() {
    return this.repoSystem;
  }

  /**
   * @param value Repository system
   * @return this
   */
  public MavenContext repoSystem(RepositorySystem value) {
    this.repoSystem = value;
    return this;
  }

  /**
   * @return Repository session
   */
  public RepositorySystemSession getRepoSession() {
    return this.repoSession;
  }

  /**
   * @param value Repository session
   * @return this
   */
  public MavenContext repoSession(RepositorySystemSession value) {
    this.repoSession = value;
    return this;
  }

  /**
   * @return Remote repositories
   */
  public List<RemoteRepository> getRemoteRepos() {
    return this.remoteRepos;
  }

  /**
   * @param value Remote repositories
   * @return this
   */
  public MavenContext remoteRepos(List<RemoteRepository> value) {
    this.remoteRepos = value;
    return this;
  }

  /**
   * @return Artifact type mappings
   */
  public Map<String, String> getArtifactTypeMappings() {
    return this.artifactTypeMappings;
  }

  /**
   * @param value Artifact type mappings
   * @return this
   */
  public MavenContext artifactTypeMappings(Map<String, String> value) {
    this.artifactTypeMappings = value;
    return this;
  }

}
