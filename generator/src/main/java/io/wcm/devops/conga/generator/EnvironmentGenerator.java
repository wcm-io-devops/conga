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

import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.environment.Node;
import io.wcm.devops.conga.model.environment.NodeRole;
import io.wcm.devops.conga.model.role.Role;
import io.wcm.devops.conga.model.util.MapMerger;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Generates file for one environment.
 */
class EnvironmentGenerator {

  private final Map<String, Role> roles;
  private final String environmentName;
  private final Environment environment;
  private final File destDir;

  public EnvironmentGenerator(Map<String, Role> roles, String environmentName, Environment environment, File destDir) {
    this.roles = roles;
    this.environmentName = environmentName;
    this.environment = environment;
    this.destDir = destDir;
  }

  public void generate() {
    for (Node node : environment.getNodes()) {
      generateNode(node);
    }
  }

  private void generateNode(Node node) {

    // validate node
    if (StringUtils.isEmpty(node.getNode())) {
      throw new GeneratorException("Missing node name in " + environmentName + ".");
    }
    for (NodeRole nodeRole : node.getRoles()) {
      Role role = roles.get(nodeRole.getRole());
      if (role == null) {
        throw new GeneratorException("Role '" + nodeRole.getRole() + "' "
            + "from " + environmentName + "/" + node.getNode() + " does not exist.");
      }
      String variant = nodeRole.getVariant();
      if (StringUtils.isNotEmpty(variant) && !role.getVariants().contains(variant)) {
        throw new GeneratorException("Variant '" + variant + "' for role '" + nodeRole.getRole() + "' "
            + "from " + environmentName + "/" + node.getNode() + " does not exist.");
      }

      // merge default values to config
      Map<String, Object> mergedConfig = MapMerger.merge(nodeRole.getConfig(), role.getConfig());

      // generate files
      File nodeDir = new File(destDir, node.getNode());
      if (!nodeDir.exists()) {
        nodeDir.mkdir();
      }

    }

  }

}
