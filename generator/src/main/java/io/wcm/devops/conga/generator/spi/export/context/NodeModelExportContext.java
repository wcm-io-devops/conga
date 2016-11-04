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

import io.wcm.devops.conga.generator.spi.context.AbstractPluginContext;
import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.environment.Node;

/**
 * Context for {@link io.wcm.devops.conga.generator.spi.export.NodeModelExportPlugin}.
 */
public final class NodeModelExportContext extends AbstractPluginContext<NodeModelExportContext> {

  private Node node;
  private Environment environment;
  private List<ExportNodeRoleData> roleData;
  private List<ExportNodeTenantData> tenantData;
  private File nodeDir;
  private Map<String, Object> config;

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
   * @return Node tenant data
   */
  public List<ExportNodeTenantData> getTenantData() {
    return this.tenantData;
  }

  /**
   * @param value Node tenant data
   * @return this
   */
  public NodeModelExportContext tenantData(List<ExportNodeTenantData> value) {
    this.tenantData = value;
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

}
