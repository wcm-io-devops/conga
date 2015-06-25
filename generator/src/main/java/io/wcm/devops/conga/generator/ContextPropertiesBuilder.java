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
package io.wcm.devops.conga.generator;

import io.wcm.devops.conga.generator.util.VariableObjectTreeResolver;
import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.environment.Node;
import io.wcm.devops.conga.model.environment.NodeRole;
import io.wcm.devops.conga.model.environment.Tenant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.rits.cloning.Cloner;

/**
 * Builds context variables
 */
final class ContextPropertiesBuilder {

  private ContextPropertiesBuilder() {
    // static methods only
  }

  /**
   * Build context variables that are global to the environment.
   * @param environmentName Environment name
   * @param environment Environment
   * @param version Environment version
   * @return Context variables map
   */
  public static Map<String, Object> buildEnvironmentContextVariables(String environmentName,
      Environment environment, String version) {
    Map<String, Object> map = new HashMap<>();

    map.put(ContextProperties.VERSION, version);
    map.put(ContextProperties.ENVIRONMENT, environmentName);

    // clone environment before resolving variables to make sure they are resolved only for this context, not for file generation
    Environment clonedEnvironemnt = new Cloner().deepClone(environment);

    // resolve all variables at any level in environment
    VariableObjectTreeResolver.resolve(clonedEnvironemnt);

    // list of nodes
    map.put(ContextProperties.NODES, clonedEnvironemnt.getNodes());
    Map<String, List<Node>> nodesByRole = new HashMap<>();
    Map<String, Map<String, List<Node>>> nodesByRoleVariant = new HashMap<>();
    for (Node node : clonedEnvironemnt.getNodes()) {
      for (NodeRole nodeRole : node.getRoles()) {
        List<Node> nodes = nodesByRole.get(nodeRole.getRole());
        if (nodes == null) {
          nodes = new ArrayList<>();
          nodesByRole.put(nodeRole.getRole(), nodes);
        }
        nodes.add(node);

        if (StringUtils.isNotEmpty(nodeRole.getVariant())) {
          Map<String, List<Node>> nodesByVariant = nodesByRoleVariant.get(nodeRole.getRole());
          if (nodesByVariant == null) {
            nodesByVariant = new HashMap<>();
            nodesByRoleVariant.put(nodeRole.getRole(), nodesByVariant);
          }
          List<Node> variantNodes = nodesByVariant.get(nodeRole.getVariant());
          if (variantNodes == null) {
            variantNodes = new ArrayList<>();
            nodesByVariant.put(nodeRole.getVariant(), variantNodes);
          }
          variantNodes.add(node);
        }
      }
    }
    map.put(ContextProperties.NODES_BY_ROLE, nodesByRole);
    map.put(ContextProperties.NODES_BY_ROLE_VARIANT, nodesByRoleVariant);

    // list of tenants
    map.put(ContextProperties.TENANTS, clonedEnvironemnt.getTenants());
    Map<String, List<Tenant>> tenantsByRole = new HashMap<>();
    for (Tenant tenant : clonedEnvironemnt.getTenants()) {
      for (String tenantRoleName : tenant.getRoles()) {
        List<Tenant> tenants = tenantsByRole.get(tenantRoleName);
        if (tenants == null) {
          tenants = new ArrayList<>();
          tenantsByRole.put(tenantRoleName, tenants);
        }
        tenants.add(tenant);
      }
    }
    map.put(ContextProperties.TENANTS_BY_ROLE, tenantsByRole);

    return map;
  }

  /**
   * Build context variables specific for a node and role/variant.
   * @param node Node
   * @param nodeRole Node role
   * @return Context variables map
   */
  public static Map<String, Object> buildCurrentContextVariables(Node node, NodeRole nodeRole) {
    Map<String, Object> map = new HashMap<>();
    map.put(ContextProperties.ROLE, nodeRole.getRole());
    map.put(ContextProperties.ROLE_VARIANT, nodeRole.getVariant());
    map.put(ContextProperties.NODE, node.getNode());
    return map;
  }


}
