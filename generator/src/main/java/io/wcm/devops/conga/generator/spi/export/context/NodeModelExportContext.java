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
package io.wcm.devops.conga.generator.spi.export.context;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.wcm.devops.conga.generator.spi.context.AbstractPluginContext;
import io.wcm.devops.conga.generator.util.VariableMapResolver;
import io.wcm.devops.conga.generator.util.VariableStringResolver;
import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.environment.Node;

/**
 * Context for {@link io.wcm.devops.conga.generator.spi.export.NodeModelExportPlugin}.
 */
public final class NodeModelExportContext extends AbstractPluginContext<NodeModelExportContext> {

  private Node node;
  private Environment environment;
  private List<ExportNodeRoleData> roleData;
  private File nodeDir;
  private Map<String, Object> config;
  private VariableStringResolver variableStringResolver;
  private VariableMapResolver variableMapResolver;
  private Map<String, String> containerVersionInfo;
  private Set<String> sensitiveConfigParameters;

  /**
   * @return Node
   */
  public Node getNode() {
    return this.node;
  }

  /**
   * @param value Node
   * @return this
   */
  public NodeModelExportContext node(Node value) {
    this.node = value;
    return this;
  }

  /**
   * @return Environment
   */
  public Environment getEnvironment() {
    return environment;
  }

  /**
   * @param value Environment
   * @return this
   */
  public NodeModelExportContext environment(Environment value) {
    environment = value;
    return this;
  }

  /**
   * @return Node role data
   */
  public List<ExportNodeRoleData> getRoleData() {
    return this.roleData;
  }

  /**
   * @param value Node role data
   * @return this
   */
  public NodeModelExportContext roleData(List<ExportNodeRoleData> value) {
    this.roleData = value;
    return this;
  }

  /**
   * @return Node files target directory
   */
  public File getNodeDir() {
    return this.nodeDir;
  }

  /**
   * @param value Node files target directory
   * @return this
   */
  public NodeModelExportContext nodeDir(File value) {
    this.nodeDir = value;
    return this;
  }

  /**
   * @return Plugin config
   */
  public Map<String, Object> getConfig() {
    return config;
  }

  /**
   * @param value Plugin config
   * @return this
   */
  public NodeModelExportContext config(Map<String, Object> value) {
    config = value;
    return this;
  }

  /**
   * @return Variable string resolver
   */
  public VariableStringResolver getVariableStringResolver() {
    return this.variableStringResolver;
  }

  /**
   * @param value Variable string resolver
   * @return this
   */
  public NodeModelExportContext variableStringResolver(VariableStringResolver value) {
    this.variableStringResolver = value;
    return this;
  }

  /**
   * @return Variable map resolver
   */
  public VariableMapResolver getVariableMapResolver() {
    return this.variableMapResolver;
  }

  /**
   * @param value Variable map resolver
   * @return this
   */
  public NodeModelExportContext variableMapResolver(VariableMapResolver value) {
    this.variableMapResolver = value;
    return this;
  }

  /**
   * @return Version information from container, e.g. configured Maven plugin versions
   */
  public Map<String, String> getContainerVersionInfo() {
    return this.containerVersionInfo;
  }

  /**
   * @param value Version information from container, e.g. configured Maven plugin versions
   * @return this
   */
  public NodeModelExportContext containerVersionInfo(Map<String, String> value) {
    this.containerVersionInfo = value;
    return this;
  }

  /**
   * @return List of configuration parameter names that contain sensitive data.
   */
  public Set<String> getSensitiveConfigParameters() {
    return this.sensitiveConfigParameters;
  }

  /**
   * @param value List of configuration parameter names that contain sensitive data
   * @return this
   */
  public NodeModelExportContext sensitiveConfigParameters(Set<String> value) {
    this.sensitiveConfigParameters = value;
    return this;
  }

}
