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
package io.wcm.devops.conga.generator.plugins.export;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import com.google.common.collect.ImmutableList;

import io.wcm.devops.conga.generator.ContextPropertiesBuilder;
import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.export.NodeModelExportPlugin;
import io.wcm.devops.conga.generator.spi.export.context.ExportNodeRoleData;
import io.wcm.devops.conga.generator.spi.export.context.ExportNodeRoleTenantData;
import io.wcm.devops.conga.generator.spi.export.context.NodeModelExportContext;
import io.wcm.devops.conga.generator.util.FileUtil;

/**
 * Exports model information for each node in YAML format.
 * This is useful e.g. for integration with Ansible.
 */
public class YamlNodeModelExport implements NodeModelExportPlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "yaml";

  private static final String MODEL_FILE = "model.yaml";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public void export(NodeModelExportContext context) {

    // generate YAML data
    Map<String, Object> modelMap = new LinkedHashMap<>();
    for (ExportNodeRoleData roleData : context.getRoleData()) {
      addRole(modelMap, roleData, context);
    }

    // save YAML file
    save(modelMap, context);
  }

  private void addRole(Map<String, Object> modelMap, ExportNodeRoleData roleData, NodeModelExportContext context) {
    String nodeDirPath = FileUtil.getCanonicalPath(context.getNodeDir());

    Map<String, Object> roleMap = new LinkedHashMap<>();
    if (StringUtils.isNotEmpty(roleData.getRoleVariant())) {
      roleMap.put("variant", roleData.getRoleVariant());
    }

    roleMap.put("files", roleData.getFiles().stream()
        .map(item -> {
          Map<String, Object> itemMap = new LinkedHashMap<String, Object>();
          itemMap.put("path", StringUtils.substring(item.getFileContext().getCanonicalPath(), nodeDirPath.length() + 1));
          if (!item.getPostProcessors().isEmpty()) {
            itemMap.put("postProcessors", ImmutableList.copyOf(item.getPostProcessors()));
          }
          return itemMap;
        })
        .collect(Collectors.toList()));

    roleMap.put("config", cleanupConfig(context.getConfig()));

    addTenants(roleMap, roleData);

    modelMap.put(roleData.getRole(), roleMap);
  }

  private void addTenants(Map<String, Object> roleMap, ExportNodeRoleData roleData) {
    Map<String, Object> tenantsMap = new LinkedHashMap<>();

    if (roleData.getTenantData() != null) {
      for (ExportNodeRoleTenantData tenantData : roleData.getTenantData()) {
        addTenant(tenantsMap, tenantData);
      }
    }

    if (!tenantsMap.isEmpty()) {
      roleMap.put("tenants", tenantsMap);
    }
  }

  private void addTenant(Map<String, Object> tenantsMap, ExportNodeRoleTenantData tenantData) {
    Map<String, Object> tenantMap = new LinkedHashMap<>();

    if (!tenantData.getRoles().isEmpty()) {
      tenantMap.put("roles", tenantData.getRoles());
    }
    tenantMap.put("config", cleanupConfig(tenantData.getConfig()));

    tenantsMap.put(tenantData.getTenant(), tenantMap);
  }

  private void save(Map<String, Object> modelMap, NodeModelExportContext context) {
    File modelFile = new File(context.getNodeDir(), MODEL_FILE);
    try (FileWriter fileWriter = new FileWriter(modelFile)) {
      new Yaml().dump(modelMap, fileWriter);
    }
    catch (Throwable ex) {
      throw new GeneratorException("Unable to write model file: " + FileUtil.getCanonicalPath(modelFile), ex);
    }
  }

  private Map<String, Object> cleanupConfig(Map<String, Object> config) {
    return ContextPropertiesBuilder.removeContextVariables(config);
  }

}
