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

import static io.wcm.devops.conga.generator.ContextProperties.ENVIRONMENT;
import static io.wcm.devops.conga.generator.ContextProperties.NODE;
import static io.wcm.devops.conga.generator.ContextProperties.NODES;
import static io.wcm.devops.conga.generator.ContextProperties.NODES_BY_ROLE;
import static io.wcm.devops.conga.generator.ContextProperties.NODES_BY_ROLE_VARIANT;
import static io.wcm.devops.conga.generator.ContextProperties.ROLE;
import static io.wcm.devops.conga.generator.ContextProperties.ROLE_VARIANT;
import static io.wcm.devops.conga.generator.ContextProperties.ROLE_VARIANTS;
import static io.wcm.devops.conga.generator.ContextProperties.TENANT;
import static io.wcm.devops.conga.generator.ContextProperties.TENANTS;
import static io.wcm.devops.conga.generator.ContextProperties.TENANTS_BY_ROLE;
import static io.wcm.devops.conga.generator.ContextProperties.TENANT_ROLES;
import static io.wcm.devops.conga.generator.ContextProperties.VERSION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.rits.cloning.Cloner;

import io.wcm.devops.conga.generator.util.VariableObjectTreeResolver;
import io.wcm.devops.conga.generator.util.VariableStringResolver;
import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.environment.Node;
import io.wcm.devops.conga.model.environment.NodeRole;
import io.wcm.devops.conga.model.environment.Tenant;
import io.wcm.devops.conga.model.util.MapMerger;

/**
 * Builds context variables
 */
public final class ContextPropertiesBuilder {

  static final Map<String, Object> EMPTY_CONTEXT_VARIABLES = ImmutableMap.<String, Object>builder()
      .put(VERSION, "")
      .put(ENVIRONMENT, "")
      .put(NODES, Collections.EMPTY_LIST)
      .put(NODES_BY_ROLE, Collections.EMPTY_MAP)
      .put(NODES_BY_ROLE_VARIANT, Collections.EMPTY_MAP)
      .put(TENANTS, Collections.EMPTY_LIST)
      .put(TENANTS_BY_ROLE, Collections.EMPTY_MAP)
      .put(ROLE, "")
      .put(ROLE_VARIANT, "")
      .put(NODE, "")
      .put(TENANT, "")
      .put(TENANT_ROLES, Collections.EMPTY_LIST)
      .build();

  private ContextPropertiesBuilder() {
    // static methods only
  }

  /**
   * Build context variables that are global to the environment.
   * @param environmentName Environment name
   * @param environment Environment
   * @param version Environment version
   * @param variableObjectTreeResolver Variable object tree resolver
   * @param variableStringResolver Variable string resolver
   * @return Context variables map
   */
  public static Map<String, Object> buildEnvironmentContextVariables(String environmentName,
      Environment environment, String version,
      VariableObjectTreeResolver variableObjectTreeResolver, VariableStringResolver variableStringResolver) {
    Map<String, Object> map = new HashMap<>(EMPTY_CONTEXT_VARIABLES);

    map.put(VERSION, version);
    map.put(ENVIRONMENT, environmentName);

    // clone environment before resolving variables to make sure they are resolved only for this context, not for file generation
    Environment clonedEnvironemnt = Cloner.standard().deepClone(environment);

    // resolve all variables at any level in environment
    variableObjectTreeResolver.resolve(clonedEnvironemnt);

    // list of nodes
    map.put(NODES, clonedEnvironemnt.getNodes());
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

        List<String> variants = nodeRole.getAggregatedVariants();
        for (String variant : variants) {
          Map<String, List<Node>> nodesByVariant = nodesByRoleVariant.get(nodeRole.getRole());
          if (nodesByVariant == null) {
            nodesByVariant = new HashMap<>();
            nodesByRoleVariant.put(nodeRole.getRole(), nodesByVariant);
          }
          List<Node> variantNodes = nodesByVariant.get(variant);
          if (variantNodes == null) {
            variantNodes = new ArrayList<>();
            nodesByVariant.put(variant, variantNodes);
          }
          variantNodes.add(node);
        }
      }
    }
    map.put(NODES_BY_ROLE, nodesByRole);
    map.put(NODES_BY_ROLE_VARIANT, nodesByRoleVariant);

    // list of tenants
    map.put(TENANTS, clonedEnvironemnt.getTenants());
    Map<String, List<Tenant>> tenantsByRole = new HashMap<>();
    for (Tenant tenant : clonedEnvironemnt.getTenants()) {

      // resolve placeholders in tentant name
      Map<String, Object> tenantConfig = MapMerger.merge(clonedEnvironemnt.getConfig(), tenant.getConfig());
      String tenantName = variableStringResolver.resolve(tenant.getTenant(), tenantConfig);
      tenant.setTenant(tenantName);

      for (String tenantRoleName : tenant.getRoles()) {
        List<Tenant> tenants = tenantsByRole.get(tenantRoleName);
        if (tenants == null) {
          tenants = new ArrayList<>();
          tenantsByRole.put(tenantRoleName, tenants);
        }
        tenants.add(tenant);
      }
    }
    map.put(TENANTS_BY_ROLE, tenantsByRole);

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
    map.put(ROLE, nodeRole.getRole());

    List<String> variants = nodeRole.getAggregatedVariants();
    if (variants.size() == 1) {
      map.put(ROLE_VARIANT, variants.get(0));
    }
    else {
      map.put(ROLE_VARIANT, null);
    }
    map.put(ROLE_VARIANTS, variants);

    map.put(NODE, node.getNode());
    return map;
  }

  /**
   * Removes all context variables.
   * @param config Configuration
   * @return Configuration
   */
  public static Map<String, Object> removeContextVariables(Map<String, Object> config) {
    Map<String, Object> map = new HashMap<>(config);
    map.remove(VERSION);
    map.remove(ENVIRONMENT);
    map.remove(NODES);
    map.remove(NODES_BY_ROLE);
    map.remove(NODES_BY_ROLE_VARIANT);
    map.remove(TENANTS);
    map.remove(TENANTS_BY_ROLE);
    map.remove(ROLE);
    map.remove(ROLE_VARIANT);
    map.remove(NODE);
    map.remove(TENANT);
    map.remove(TENANT_ROLES);
    return map;
  }

  /**
   * Get map with all context variables set to empty values.
   * @return Map with context variables
   */
  public static Map<String, Object> getEmptyContextVariables() {
    return EMPTY_CONTEXT_VARIABLES;
  }

}
