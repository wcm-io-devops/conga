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

import io.wcm.devops.conga.generator.handlebars.HandlebarsManager;
import io.wcm.devops.conga.generator.plugins.multiply.NoneMultiply;
import io.wcm.devops.conga.generator.spi.MultiplyContext;
import io.wcm.devops.conga.generator.spi.MultiplyPlugin;
import io.wcm.devops.conga.generator.spi.ValidationException;
import io.wcm.devops.conga.generator.util.FileUtil;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.environment.Node;
import io.wcm.devops.conga.model.environment.NodeRole;
import io.wcm.devops.conga.model.resolver.VariableResolver;
import io.wcm.devops.conga.model.role.Role;
import io.wcm.devops.conga.model.role.RoleFile;
import io.wcm.devops.conga.model.util.MapMerger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

/**
 * Generates file for one environment.
 */
class EnvironmentGenerator {

  private final Map<String, Role> roles;
  private final String environmentName;
  private final Environment environment;
  private final File destDir;
  private final PluginManager pluginManager;
  private final HandlebarsManager handlebarsManager;
  private final MultiplyPlugin defaultMultiplyPlugin;
  private final Logger log;

  public EnvironmentGenerator(Map<String, Role> roles, String environmentName, Environment environment,
      File destDir, PluginManager pluginManager, HandlebarsManager handlebarsManager, Logger log) {
    this.roles = roles;
    this.environmentName = environmentName;
    this.environment = environment;
    this.destDir = destDir;
    this.pluginManager = pluginManager;
    this.handlebarsManager = handlebarsManager;
    this.defaultMultiplyPlugin = pluginManager.get(NoneMultiply.NAME, MultiplyPlugin.class);
    this.log = log;
  }

  public void generate() {
    log.info("Genrate environment '{}'...", environmentName);

    for (Node node : environment.getNodes()) {
      generateNode(node);
    }
  }

  private void generateNode(Node node) {
    if (StringUtils.isEmpty(node.getNode())) {
      throw new GeneratorException("Missing node name in " + environmentName + ".");
    }

    log.info("Generate node '{}'...", node.getNode());

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

      // define variable map for filename placeholders (only default properties)
      Map<String, Object> contextVariables = populateContextVariables(node, nodeRole);

      // merge default values to config
      Map<String, Object> mergedConfig = MapMerger.merge(nodeRole.getConfig(), role.getConfig());

      // generate files
      File nodeDir = new File(destDir, node.getNode());
      if (!nodeDir.exists()) {
        nodeDir.mkdir();
      }
      for (RoleFile roleFile : role.getFiles()) {
        if (roleFile.getVariants().isEmpty() || roleFile.getVariants().contains(variant)) {
          Template template = getHandlebarsTemplate(role, roleFile, nodeRole);
          multiplyFiles(role, roleFile, mergedConfig, contextVariables, nodeDir, template);
        }
      }
    }
  }

  private Map<String, Object> populateContextVariables(Node node, NodeRole nodeRole) {
    Map<String, Object> contextVariables = new HashMap<>();
    contextVariables.put(ContextProperties.ROLE, nodeRole.getRole());
    contextVariables.put(ContextProperties.ROLE_VARIANT, nodeRole.getVariant());
    contextVariables.put(ContextProperties.ENVIRONMENT, environmentName);
    contextVariables.put(ContextProperties.NODE, node.getNode());
    contextVariables.put(ContextProperties.TENANTS, environment.getTenants());
    return contextVariables;
  }

  private Template getHandlebarsTemplate(Role role, RoleFile roleFile, NodeRole nodeRole) {
    String templateFile = roleFile.getTemplate();
    if (StringUtils.isEmpty(templateFile)) {
      throw new GeneratorException("No template defined for file: " + nodeRole.getRole() + "/" + roleFile.getFile());
    }
    if (StringUtils.isNotEmpty(role.getTemplateDir())) {
      templateFile = FilenameUtils.concat(role.getTemplateDir(), templateFile);
    }
    try {
      Handlebars handlebars = handlebarsManager.get(roleFile.getCharset());
      return handlebars.compile(templateFile);
    }
    catch (IOException ex) {
      throw new GeneratorException("Unable to compile handlebars template: " + nodeRole.getRole() + "/" + roleFile.getFile(), ex);
    }
  }

  private void multiplyFiles(Role role, RoleFile roleFile, Map<String, Object> config, Map<String,Object> contextVariables,
      File nodeDir, Template template) {
    MultiplyPlugin multiplyPlugin = defaultMultiplyPlugin;
    if (StringUtils.isNotEmpty(roleFile.getMultiply())) {
      multiplyPlugin = pluginManager.get(roleFile.getMultiply(), MultiplyPlugin.class);
    }

    List<MultiplyContext> contexts = multiplyPlugin.multiply(role, roleFile, environment, config, contextVariables);
    for (MultiplyContext context : contexts) {

      // replace placeholders in dir/filename with context variables
      String dir = VariableResolver.replaceVariables(roleFile.getDir(), context.getContextVariables());
      String file = VariableResolver.replaceVariables(roleFile.getFile(), context.getContextVariables());

      // merge context variables in configuration
      Map<String, Object> mergedConfig = MapMerger.merge(context.getContextVariables(), context.getConfig());

      generateFile(roleFile, dir, file, mergedConfig, nodeDir, template);
    }
  }

  private void generateFile(RoleFile roleFile, String dir, String fileName, Map<String, Object> config, File nodeDir, Template template) {
    File file = new File(nodeDir, FilenameUtils.concat(dir, fileName));
    if (file.exists()) {
      throw new GeneratorException("File exists already, check for file name clashes: " + FileUtil.getCanonicalPath(file));
    }
    FileGenerator fileGenerator = new FileGenerator(nodeDir, file, roleFile, config, template, pluginManager, log);
    try {
      fileGenerator.generate();
    }
    catch (ValidationException ex) {
      throw new GeneratorException("File validation failed " + FileUtil.getCanonicalPath(file) + " - " + ex.getMessage());
    }
    catch (Throwable ex) {
      throw new GeneratorException("Unable to generate file: " + FileUtil.getCanonicalPath(file) + "", ex);
    }
  }

}
