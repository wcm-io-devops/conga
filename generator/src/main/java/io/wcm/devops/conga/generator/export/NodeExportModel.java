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
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.wcm.devops.conga.generator.ContextPropertiesBuilder;
import io.wcm.devops.conga.generator.GeneratedFileContext;
import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.util.FileUtil;
import io.wcm.devops.conga.generator.util.VariableMapResolver;

/**
 * Generates a YAML file with all "model" information for each node of the environment.
 * This is useful for consuming it in infrastructure automation tools like Ansible.
 */
public final class NodeExportModel {

  private final File nodeDir;
  private final String nodeDirPath;
  private final Map<String, Object> modelMap = new LinkedHashMap<>();

  /**
   * @param nodeDir Target directory for node files
   */
  public NodeExportModel(File nodeDir) {
    this.nodeDir = nodeDir;
    this.nodeDirPath = FileUtil.getCanonicalPath(nodeDir);
  }

  /**
   * Add role information
   * @param role Role name
   * @param roleVariant Role variant name
   * @param files Generated files
   * @param config Merged configuration
   */
  public void addRole(String role, String roleVariant,
      List<GeneratedFileContext> files,
      Map<String, Object> config) {

    Map<String, Object> roleMap = new LinkedHashMap<>();
    if (StringUtils.isNotEmpty(roleVariant)) {
      roleMap.put("variant", roleVariant);
    }

    roleMap.put("files", files.stream()
        .map(item -> ImmutableMap.<String, Object>builder()
            .put("path", StringUtils.substring(item.getFileContext().getCanonicalPath(), nodeDirPath.length() + 1))
            .put("postProcessors", ImmutableList.copyOf(item.getPostProcessors()))
            .build())
        .collect(Collectors.toList()));

    // resolve variables in configuration, and remove context properites
    Map<String, Object> resolvedConfig = VariableMapResolver.resolve(config, false);
    resolvedConfig = ContextPropertiesBuilder.removeContextVariables(resolvedConfig);
    roleMap.put("config", resolvedConfig);

    modelMap.put(role, roleMap);
  }

  /**
   * Generate model YAML file.
   */
  public void generate() {
    File modelFile = new File(nodeDir, "model.yml");
    try (FileWriter fileWriter = new FileWriter(modelFile)) {
      new Yaml().dump(modelMap, fileWriter);
    }
    catch (Throwable ex) {
      throw new GeneratorException("Unable to write model file: " + FileUtil.getCanonicalPath(modelFile), ex);
    }
  }

}
