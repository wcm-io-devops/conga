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

import io.wcm.devops.conga.model.shared.AbstractConfigurable;

import java.util.ArrayList;
import java.util.List;

/**
 * Environment definition.
 */
public final class Environment extends AbstractConfigurable {

  private List<Node> nodes = new ArrayList<>();
  private List<RoleConfig> roleConfig = new ArrayList<>();
  private List<Tenant> tenants = new ArrayList<>();

  public List<Node> getNodes() {
    return this.nodes;
  }

  public void setNodes(List<Node> nodes) {
    this.nodes = nodes;
  }

  public List<RoleConfig> getRoleConfig() {
    return this.roleConfig;
  }

  public void setRoleConfig(List<RoleConfig> roleConfig) {
    this.roleConfig = roleConfig;
  }

  public List<Tenant> getTenants() {
    return this.tenants;
  }

  public void setTenants(List<Tenant> tenants) {
    this.tenants = tenants;
  }

}
