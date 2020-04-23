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
package io.wcm.devops.conga.model.environment;

import static io.wcm.devops.conga.model.util.DefaultUtil.defaultEmptyList;
import static io.wcm.devops.conga.model.util.DefaultUtil.defaultEmptyMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.wcm.devops.conga.model.shared.AbstractConfigurable;

/**
 * Defines an environment with a set of nodes and configuration.
 * The filename of the environment YAML file is the environment name, it's not included in the model.
 */
public final class Environment extends AbstractConfigurable {

  private List<Node> nodes = new ArrayList<>();
  private List<RoleConfig> roleConfig = new ArrayList<>();
  private List<Tenant> tenants = new ArrayList<>();
  private List<String> dependencies = new ArrayList<>();
  private Map<String, Map<String, Object>> pluginConfig = new HashMap<>();

  /**
   * Defines nodes for the environment.
   * @return List of nodes
   */
  public List<Node> getNodes() {
    return this.nodes;
  }

  public void setNodes(List<Node> nodes) {
    this.nodes = defaultEmptyList(nodes);
  }

  /**
   * Defines role-specific configuration. In this section it is possible to define configuration parameters that affect
   * each node that has this role defined.
   * @return Configurations per role
   */
  public List<RoleConfig> getRoleConfig() {
    return this.roleConfig;
  }

  public void setRoleConfig(List<RoleConfig> roleConfig) {
    this.roleConfig = defaultEmptyList(roleConfig);
  }

  /**
   * Defines a list of tenants for configuration files that have tenant-specific system configurations.
   * E.g. vhosts files for Apache Webserver.
   * @return List of tenants
   */
  public List<Tenant> getTenants() {
    return this.tenants;
  }

  public void setTenants(List<Tenant> tenants) {
    this.tenants = defaultEmptyList(tenants);
  }

  /**
   * Defines a list of file URLs pointing to JAR files with CONGA definitions that contain dependencies (e.g. roles) to
   * be used in this environment.
   * @return List of dependencies
   */
  public List<String> getDependencies() {
    return this.dependencies;
  }

  public void setDependencies(List<String> dependencies) {
    this.dependencies = defaultEmptyList(dependencies);
  }

  /**
   * Defines a map of configuration parameters for CONGA plugins.
   * <p>
   * The outer map contains the plugin names as keys, and the configuration for this plugin as value.
   * The inner map contains the key/value pairs for the plugin configuration.
   * </p>
   * <p>
   * The configurations are merged with the configuration parameters defined in the POM.
   * </p>
   * @return Plugin configuration parameter map
   */
  public Map<String, Map<String, Object>> getPluginConfig() {
    return this.pluginConfig;
  }

  /**
   * @param pluginConfig Plugin configuration parameter map
   */
  public void setPluginConfig(Map<String, Map<String, Object>> pluginConfig) {
    this.pluginConfig = defaultEmptyMap(pluginConfig);
  }

}
