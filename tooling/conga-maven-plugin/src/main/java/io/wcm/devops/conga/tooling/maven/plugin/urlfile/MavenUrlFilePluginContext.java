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

import java.util.List;

import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

/**
 * Contains maven-specific context objects.
 */
public final class MavenUrlFilePluginContext {

  private MavenProject project;
  private RepositorySystem repoSystem;
  private RepositorySystemSession repoSession;
  private List<RemoteRepository> remoteRepos;

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
  public MavenUrlFilePluginContext project(MavenProject value) {
    this.project = value;
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
  public MavenUrlFilePluginContext repoSystem(RepositorySystem value) {
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
  public MavenUrlFilePluginContext repoSession(RepositorySystemSession value) {
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
  public MavenUrlFilePluginContext remoteRepos(List<RemoteRepository> value) {
    this.remoteRepos = value;
    return this;
  }

}
