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
   * Gets Maven project.
   * @return Maven project
   */
  public MavenProject getProject() {
    return this.project;
  }

  /**
   * Sets Maven project.
   * @param value Maven project
   * @return this
   */
  public MavenContext project(MavenProject value) {
    this.project = value;
    return this;
  }

  /**
   * Gets Maven session.
   * @return Maven Session
   */
  public MavenSession getSession() {
    return this.session;
  }

  /**
   * Sets Maven session.
   * @param value Maven Session
   * @return this
   */
  public MavenContext session(MavenSession value) {
    this.session = value;
    return this;
  }

  /**
   * Gets repository system.
   * @return Repository system
   */
  public org.apache.maven.repository.RepositorySystem getRepositorySystem() {
    return this.repositorySystem;
  }

  /**
   * Sets repository system.
   * @param value Repository system
   * @return this
   */
  public MavenContext setRepositorySystem(org.apache.maven.repository.RepositorySystem value) {
    this.repositorySystem = value;
    return this;
  }

  /**
   * Gets resolution error handler.
   * @return Resolution error handler
   */
  public ResolutionErrorHandler getResolutionErrorHandler() {
    return this.resolutionErrorHandler;
  }

  /**
   * Sets resolution error handler.
   * @param value Resolution error handler
   * @return this
   */
  public MavenContext resolutionErrorHandler(ResolutionErrorHandler value) {
    this.resolutionErrorHandler = value;
    return this;
  }

  /**
   * Gets build context.
   * @return Build context
   */
  public BuildContext getBuildContext() {
    return this.buildContext;
  }

  /**
   * Sets build context.
   * @param value Build context
   * @return this
   */
  public MavenContext buildContext(BuildContext value) {
    this.buildContext = value;
    return this;
  }

  /**
   * Gets log.
   * @return Log
   */
  public Log getLog() {
    return this.log;
  }

  /**
   * Sets log.
   * @param value Log
   * @return this
   */
  public MavenContext log(Log value) {
    this.log = value;
    return this;
  }

  /**
   * Gets repository system.
   * @return Repository system
   */
  public RepositorySystem getRepoSystem() {
    return this.repoSystem;
  }

  /**
   * Sets repository system.
   * @param value Repository system
   * @return this
   */
  public MavenContext repoSystem(RepositorySystem value) {
    this.repoSystem = value;
    return this;
  }

  /**
   * Gets repository session.
   * @return Repository session
   */
  public RepositorySystemSession getRepoSession() {
    return this.repoSession;
  }

  /**
   * Sets repository session.
   * @param value Repository session
   * @return this
   */
  public MavenContext repoSession(RepositorySystemSession value) {
    this.repoSession = value;
    return this;
  }

  /**
   * Gets remote repositories.
   * @return Remote repositories
   */
  public List<RemoteRepository> getRemoteRepos() {
    return this.remoteRepos;
  }

  /**
   * Sets remote repositories.
   * @param value Remote repositories
   * @return this
   */
  public MavenContext remoteRepos(List<RemoteRepository> value) {
    this.remoteRepos = value;
    return this;
  }

  /**
   * Gets artifact type mappings.
   * @return Artifact type mappings
   */
  public Map<String, String> getArtifactTypeMappings() {
    return this.artifactTypeMappings;
  }

  /**
   * Sets artifact type mappings.
   * @param value Artifact type mappings
   * @return this
   */
  public MavenContext artifactTypeMappings(Map<String, String> value) {
    this.artifactTypeMappings = value;
    return this;
  }

}
