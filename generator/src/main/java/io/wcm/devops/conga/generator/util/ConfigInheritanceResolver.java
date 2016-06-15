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
package io.wcm.devops.conga.generator.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.environment.Node;
import io.wcm.devops.conga.model.environment.NodeRole;
import io.wcm.devops.conga.model.environment.RoleConfig;
import io.wcm.devops.conga.model.shared.Configurable;
import io.wcm.devops.conga.model.util.MapMerger;

/**
 * Iterates over all {@link Configurable} items in the object tree.
 * Configuration from parent objects is inherited to client objects.
 * Variables are not resolved though, this is done during configuration generation process.
 */
public final class ConfigInheritanceResolver extends AbstractConfigurableObjectTreeProcessor<Map<String, Object>> {

  private static final ConfigurableProcessor<Map<String, Object>> PROCESSOR = new ConfigurableProcessor<Map<String, Object>>() {
    @Override
    public Map<String, Object> process(Configurable configurable, Map<String, Object> parentConfig) {
      Map<String, Object> mergedConfig = MapMerger.merge(configurable.getConfig(), parentConfig);
      configurable.setConfig(mergedConfig);
      return mergedConfig;
    }
  };

  private ConfigInheritanceResolver() {
    // static methods only
  }

  /**
   * Inherit all configurations.
   * @param model Model with {@link Configurable} instances at any nested level.
   */
  public static void resolve(Object model) {
    if (model instanceof Environment) {
      resolveEnvironment((Environment)model);
    }
    new ConfigInheritanceResolver().process(model, PROCESSOR, new HashMap<>());
  }

  /**
   * Special handling for environment objects concerning global role configuration.
   * @param environment Environment
   */
  private static void resolveEnvironment(Environment environment) {
    ConfigInheritanceResolver resolver = new ConfigInheritanceResolver();

    Map<String, Object> rootConfig = environment.getConfig();
    resolver.process(environment.getTenants(), PROCESSOR, rootConfig);
    resolver.process(environment.getRoleConfig(), PROCESSOR, rootConfig);

    for (Node node : environment.getNodes()) {
      Map<String, Object> rawNodeConfig = node.getConfig();
      PROCESSOR.process(node, rootConfig);
      for (NodeRole nodeRole : node.getRoles()) {
        Map<String, Object> globalRoleConfig = getGlobalRoleConfig(environment, nodeRole.getRole());
        Map<String, Object> mergedConfig = MapMerger.merge(rawNodeConfig, MapMerger.merge(globalRoleConfig, rootConfig));
        resolver.process(nodeRole, PROCESSOR, mergedConfig);
      }
    }
  }

  private static Map<String, Object> getGlobalRoleConfig(Environment environment, String roleName) {
    for (RoleConfig roleConfig : environment.getRoleConfig()) {
      if (StringUtils.equals(roleConfig.getRole(), roleName)) {
        return roleConfig.getConfig();
      }
    }
    return new HashMap<>();
  }

}
