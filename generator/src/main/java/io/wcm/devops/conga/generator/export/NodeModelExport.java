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
package io.wcm.devops.conga.generator.export;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.wcm.devops.conga.generator.ContextProperties;
import io.wcm.devops.conga.generator.spi.context.PluginContextOptions;
import io.wcm.devops.conga.generator.spi.export.NodeModelExportPlugin;
import io.wcm.devops.conga.generator.spi.export.context.ExportNodeRoleData;
import io.wcm.devops.conga.generator.spi.export.context.ExportNodeRoleTenantData;
import io.wcm.devops.conga.generator.spi.export.context.NodeModelExportContext;
import io.wcm.devops.conga.generator.spi.yaml.context.YamlRepresenter;
import io.wcm.devops.conga.generator.util.ObjectCloner;
import io.wcm.devops.conga.generator.util.VariableMapResolver;
import io.wcm.devops.conga.generator.util.VariableStringResolver;
import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.environment.Node;
import io.wcm.devops.conga.model.environment.Tenant;
import io.wcm.devops.conga.model.util.MapMerger;

/**
 * Managers model exports via the model export plugins.
 */
public final class NodeModelExport {

  private final File nodeDir;
  private final Node node;
  private final Environment environment;
  private final List<NodeModelExportPlugin> nodeModelExportPlugins = new ArrayList<>();
  private final VariableStringResolver variableStringResolver;
  private final VariableMapResolver variableMapResolver;
  private final Map<String, String> containerVersionInfo;
  private final PluginContextOptions pluginContextOptions;
  private final Set<String> sensitiveConfigParameters;
  private final YamlRepresenter yamlRepresenter;

  private final List<ExportNodeRoleData> roleData = new ArrayList<>();

  /**
   * @param nodeDir Target directory for node
   * @param node Node
   * @param environment Environment
   * @param modelExport Model export
   * @param variableStringResolver Variable string resolver
   * @param variableMapResolver Variable map resolver
   * @param containerVersionInfo Version information from container, e.g. configured Maven plugin versions
   * @param pluginContextOptions Plugin context options
   * @param sensitiveConfigParameters Combined list of all sensitive config parameter names from all roles in the
   *          environment.
   * @param yamlRepresenter YAML representer
   */
  @SuppressWarnings("java:S107") // allow many parameters
  public NodeModelExport(File nodeDir, Node node, Environment environment, ModelExport modelExport,
      VariableStringResolver variableStringResolver, VariableMapResolver variableMapResolver,
      Map<String, String> containerVersionInfo, PluginContextOptions pluginContextOptions,
      Set<String> sensitiveConfigParameters, YamlRepresenter yamlRepresenter) {
    this.node = node;
    this.environment = environment;
    this.nodeDir = nodeDir;
    this.variableStringResolver = variableStringResolver;
    this.variableMapResolver = variableMapResolver;
    this.containerVersionInfo = containerVersionInfo;
    this.pluginContextOptions = pluginContextOptions;
    this.sensitiveConfigParameters = sensitiveConfigParameters;
    this.yamlRepresenter = yamlRepresenter;

    // get export plugins
    if (modelExport != null) {
      List<String> nodeExportPlugins = modelExport.getNode();
      if (nodeExportPlugins != null) {
        for (String nodeExportPlugin : nodeExportPlugins) {
          nodeModelExportPlugins.add(pluginContextOptions.getPluginManager().get(nodeExportPlugin, NodeModelExportPlugin.class));
        }
      }
    }
  }

  private boolean isActive() {
    return !nodeModelExportPlugins.isEmpty();
  }

  /**
   * Add role information
   * @param role Role name
   * @param roleVariants Role variant name
   * @param config Merged configuration (unresolved)
   * @return Node role data
   */
  public ExportNodeRoleData addRole(String role, List<String> roleVariants, Map<String, Object> config) {
    if (!isActive()) {
      return new ExportNodeRoleData();
    }

    // clone config to make sure it is not tampered by the plugin
    Map<String, Object> clonedConfig = ObjectCloner.deepClone(config);

    // resolve variables in configuration, and remove context properties
    Map<String, Object> resolvedNodeConfig = variableMapResolver.resolve(clonedConfig, false);

    // generate tenants and tenant config
    List<ExportNodeRoleTenantData> tenantData = new ArrayList<>();
    for (Tenant tenant : environment.getTenants()) {
      Map<String, Object> tenantConfig = MapMerger.merge(tenant.getConfig(), clonedConfig);

      // set tenant-specific context variables
      tenantConfig.put(ContextProperties.TENANT, variableStringResolver.resolve(tenant.getTenant(), tenantConfig));
      tenantConfig.put(ContextProperties.TENANT_ROLES, tenant.getRoles());

      // resolve variables in configuration
      Map<String, Object> resolvedTenantConfig = variableMapResolver.resolve(tenantConfig, false);

      tenantData.add(new ExportNodeRoleTenantData()
          .tenant(tenant.getTenant())
          .roles(tenant.getRoles())
          .config(resolvedTenantConfig));
    }

    ExportNodeRoleData item = new ExportNodeRoleData()
        .role(role)
        .roleVariant(roleVariants)
        .config(resolvedNodeConfig)
        .tenantData(tenantData);
    roleData.add(item);
    return item;
  }

  /**
   * Generate model YAML file.
   */
  public void generate() {
    if (!isActive()) {
      return;
    }

    for (NodeModelExportPlugin plugin : nodeModelExportPlugins) {
      plugin.export(new NodeModelExportContext()
          .pluginContextOptions(pluginContextOptions)
          .node(node)
          .environment(environment)
          .roleData(roleData)
          .nodeDir(nodeDir)
          .variableStringResolver(variableStringResolver)
          .variableMapResolver(variableMapResolver)
          .containerVersionInfo(containerVersionInfo)
          .sensitiveConfigParameters(sensitiveConfigParameters)
          .yamlRepresenter(yamlRepresenter));
    }
  }

}
