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

import com.rits.cloning.Cloner;

import io.wcm.devops.conga.generator.spi.export.NodeModelExportPlugin;
import io.wcm.devops.conga.generator.spi.export.context.ExportNodeRoleData;
import io.wcm.devops.conga.generator.spi.export.context.NodeModelExportContext;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.generator.util.VariableMapResolver;
import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.environment.ExportModel;
import io.wcm.devops.conga.model.environment.Node;

/**
 * Managers model exports via the model export plugins.
 */
public final class NodeModelExport {

  private final File nodeDir;
  private final Node node;
  private final Environment environment;
  private final List<NodeModelExportPluginItem> nodeModelExportPlugins = new ArrayList<>();

  private final List<ExportNodeRoleData> roleData = new ArrayList<>();

  /**
   * @param nodeDir Target directory for node
   * @param node Node
   * @param environment Environment
   * @param pluginManager Plugin manager
   */
  public NodeModelExport(File nodeDir, Node node, Environment environment, PluginManager pluginManager) {
    this.node = node;
    this.environment = environment;
    this.nodeDir = nodeDir;

    // get export plugins
    List<ExportModel> exportModels = environment.getExportModel();
    if (exportModels != null) {
      for (ExportModel exportModel : exportModels) {
        NodeModelExportPluginItem item = new NodeModelExportPluginItem();
        item.plugin = pluginManager.get(exportModel.getNode(), NodeModelExportPlugin.class);
        item.config = exportModel.getConfig();
        nodeModelExportPlugins.add(item);
      }
    }
  }

  private boolean isActive() {
    return !nodeModelExportPlugins.isEmpty();
  }

  /**
   * Add role information
   * @param role Role name
   * @param roleVariant Role variant name
   * @param config Merged configuration (unresolved)
   */
  public ExportNodeRoleData addRole(String role, String roleVariant, Map<String, Object> config) {
    if (!isActive()) {
      return new ExportNodeRoleData();
    }

    // clone config to make sure it is not tampered by the plugin
    Map<String, Object> clonedConfig = Cloner.standard().deepClone(config);

    // resolve variables in configuration, and remove context properites
    Map<String, Object> resolvedConfig = VariableMapResolver.resolve(clonedConfig, false);

    ExportNodeRoleData item = new ExportNodeRoleData()
        .role(role)
        .roleVariant(roleVariant)
        .config(resolvedConfig);
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

    for (NodeModelExportPluginItem item : nodeModelExportPlugins) {
      item.plugin.export(new NodeModelExportContext()
          .node(node)
          .environment(environment)
          .roleData(roleData)
          .nodeDir(nodeDir)
          .config(item.config));
    }
  }

  private class NodeModelExportPluginItem {
    private NodeModelExportPlugin plugin;
    private Map<String, Object> config;
  }

}
