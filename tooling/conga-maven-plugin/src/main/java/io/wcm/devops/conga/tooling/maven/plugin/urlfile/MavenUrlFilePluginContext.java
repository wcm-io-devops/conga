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

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.repository.RepositorySystem;

/**
 * Contains maven-specific context objects.
 */
public final class MavenUrlFilePluginContext {

  private RepositorySystem repository;
  private ArtifactRepository localRepository;
  private java.util.List<ArtifactRepository> remoteRepositories;

  /**
   * @return Maven repository
   */
  public RepositorySystem getRepository() {
    return this.repository;
  }

  /**
   * @param value Maven repository
   * @return this
   */
  public MavenUrlFilePluginContext repository(RepositorySystem value) {
    this.repository = value;
    return this;
  }

  /**
   * @return Local repository
   */
  public ArtifactRepository getLocalRepository() {
    return this.localRepository;
  }

  /**
   * @param value Local repository
   * @return this
   */
  public MavenUrlFilePluginContext localRepository(ArtifactRepository value) {
    this.localRepository = value;
    return this;
  }

  /**
   * @return Remote repositories
   */
  public java.util.List<ArtifactRepository> getRemoteRepositories() {
    return this.remoteRepositories;
  }

  /**
   * @param value Remote repositories
   * @return this
   */
  public MavenUrlFilePluginContext remoteRepositories(java.util.List<ArtifactRepository> value) {
    this.remoteRepositories = value;
    return this;
  }

}
