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
import io.wcm.devops.conga.model.shared.AbstractConfigurable;

import java.util.ArrayList;
import java.util.List;

/**
 * Environment node. A node is a system to deploy to, e.g. a physical machine, virtual machine, Docker container or any
 * other deployment target.
 */
public final class Node extends AbstractConfigurable {

  private String node;
  private List<String> nodes = new ArrayList<>();
  private List<NodeRole> roles = new ArrayList<>();

  /**
   * Defines the node name. This is usually a host name or any other unique name identifying the node.
   * @return Node name
   */
  public String getNode() {
    return this.node;
  }

  public void setNode(String node) {
    this.node = node;
  }

  /**
   * Defines multiple node names. This is useful if the same set of roles, role variants and configuration apply
   * to multiple nodes. In this case you can define a single node definition with multiple node names.
   * The single node name property must nost be used in this case.
   * @return List of node names
   */
  public List<String> getNodes() {
    return this.nodes;
  }

  public void setNodes(List<String> nodes) {
    this.nodes = defaultEmptyList(nodes);
  }

  /**
   * Defines roles to be used by this node.
   * @return Role assignments for node
   */
  public List<NodeRole> getRoles() {
    return this.roles;
  }

  public void setRoles(List<NodeRole> roles) {
    this.roles = defaultEmptyList(roles);
  }

}
