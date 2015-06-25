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
import io.wcm.devops.conga.generator.plugins.handlebars.escaping.NoneEscapingStrategy;
import io.wcm.devops.conga.generator.plugins.multiply.NoneMultiply;
import io.wcm.devops.conga.generator.spi.MultiplyPlugin;
import io.wcm.devops.conga.generator.spi.ValidationException;
import io.wcm.devops.conga.generator.spi.context.MultiplyContext;
import io.wcm.devops.conga.generator.spi.handlebars.EscapingStrategyPlugin;
import io.wcm.devops.conga.generator.util.FileUtil;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.generator.util.VariableMapResolver;
import io.wcm.devops.conga.generator.util.VariableStringResolver;
import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.environment.Node;
import io.wcm.devops.conga.model.environment.NodeRole;
import io.wcm.devops.conga.model.role.Role;
import io.wcm.devops.conga.model.role.RoleFile;
import io.wcm.devops.conga.model.role.RoleVariant;
import io.wcm.devops.conga.model.util.MapMerger;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.common.collect.ImmutableMap;

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
  private final String version;
  private final List<String> dependencyVersions;
  private final Logger log;

  private final Map<String, Object> environmentContextProperties;
  private final Set<String> generatedFilePaths = new HashSet<>();

  public EnvironmentGenerator(Map<String, Role> roles, String environmentName, Environment environment,
      File destDir, PluginManager pluginManager, HandlebarsManager handlebarsManager,
      String version, List<String> dependencyVersions, Logger log) {
    this.roles = roles;
    this.environmentName = environmentName;
    this.environment = environment;
    this.destDir = destDir;
    this.pluginManager = pluginManager;
    this.handlebarsManager = handlebarsManager;
    this.defaultMultiplyPlugin = pluginManager.get(NoneMultiply.NAME, MultiplyPlugin.class);
    this.version = version;
    this.dependencyVersions = dependencyVersions;
    this.log = log;
    this.environmentContextProperties = ImmutableMap.copyOf(
        ContextPropertiesBuilder.buildEnvironmentContextVariables(environmentName, environment, version));
  }

  public void generate() {
    log.info("");
    log.info("Generate environment '{}'...", environmentName);

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
      RoleVariant roleVariant = getRoleVariant(role, variant, nodeRole.getRole(), node);

      // merge default values to config
      Map<String, Object> roleConfig = roleVariant != null ? roleVariant.getConfig() : role.getConfig();
      Map<String, Object> mergedConfig = MapMerger.merge(nodeRole.getConfig(), roleConfig);

      // additionally set context variables
      mergedConfig.putAll(environmentContextProperties);
      mergedConfig.putAll(ContextPropertiesBuilder.buildCurrentContextVariables(node, nodeRole));

      // generate files
      File nodeDir = new File(destDir, node.getNode());
      if (!nodeDir.exists()) {
        nodeDir.mkdir();
      }
      for (RoleFile roleFile : role.getFiles()) {
        if (roleFile.getVariants().isEmpty() || roleFile.getVariants().contains(variant)) {
          Template template = getHandlebarsTemplate(role, roleFile, nodeRole);
          multiplyFiles(role, roleFile, mergedConfig, nodeDir, template);
        }
      }
    }
  }

  private RoleVariant getRoleVariant(Role role, String variant, String roleName, Node node) {
    if (StringUtils.isEmpty(variant)) {
      return null;
    }
    for (RoleVariant roleVariant : role.getVariants()) {
      if (StringUtils.equals(variant, roleVariant.getVariant())) {
        return roleVariant;
      }
    }
    throw new GeneratorException("Variant '" + variant + "' for role '" + roleName + "' "
        + "from " + environmentName + "/" + node.getNode() + " does not exist.");
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
      Handlebars handlebars = handlebarsManager.get(getEscapingStrategy(roleFile), roleFile.getCharset());
      return handlebars.compile(templateFile);
    }
    catch (IOException ex) {
      throw new GeneratorException("Unable to compile handlebars template: " + nodeRole.getRole() + "/" + roleFile.getFile(), ex);
    }
  }

  /**
   * Get escaping strategy for file. If one is explicitly defined in role definition use this.
   * Otherwise get the best-matching by file extension.
   * @param roleFile Role file
   * @return Escaping Strategy (never null)
   */
  private String getEscapingStrategy(RoleFile roleFile) {
    if (StringUtils.isNotEmpty(roleFile.getEscapingStrategy())) {
      return roleFile.getEscapingStrategy();
    }
    String fileExtension = FilenameUtils.getExtension(roleFile.getFile());
    return pluginManager.getAll(EscapingStrategyPlugin.class).stream()
        .filter(plugin -> !StringUtils.equals(plugin.getName(), NoneEscapingStrategy.NAME))
        .filter(plugin -> plugin.accepts(fileExtension))
        .findFirst().orElse(pluginManager.get(NoneEscapingStrategy.NAME, EscapingStrategyPlugin.class))
        .getName();
  }

  private void multiplyFiles(Role role, RoleFile roleFile, Map<String, Object> config, File nodeDir, Template template) {
    MultiplyPlugin multiplyPlugin = defaultMultiplyPlugin;
    if (StringUtils.isNotEmpty(roleFile.getMultiply())) {
      multiplyPlugin = pluginManager.get(roleFile.getMultiply(), MultiplyPlugin.class);
    }

    MultiplyContext multiplyContext = new MultiplyContext()
    .role(role)
    .roleFile(roleFile)
    .environment(environment)
    .config(config)
    .logger(log);

    List<Map<String, Object>> muliplyConfigs = multiplyPlugin.multiply(multiplyContext);
    for (Map<String, Object> muliplyConfig : muliplyConfigs) {

      // resolve variables
      Map<String, Object> resolvedConfig = VariableMapResolver.resolve(muliplyConfig);

      // replace placeholders in dir/filename with context variables
      String dir = VariableStringResolver.resolve(roleFile.getDir(), resolvedConfig);
      String file = VariableStringResolver.resolve(roleFile.getFile(), resolvedConfig);

      generateFile(roleFile, dir, file, resolvedConfig, nodeDir, template);
    }
  }

  private void generateFile(RoleFile roleFile, String dir, String fileName, Map<String, Object> config, File nodeDir, Template template) {
    File file = new File(nodeDir, dir != null ? FilenameUtils.concat(dir, fileName) : fileName);
    if (generatedFilePaths.contains(FileUtil.getCanonicalPath(file))) {
      throw new GeneratorException("File was generated already, check for file name clashes: " + FileUtil.getCanonicalPath(file));
    }
    if (file.exists()) {
      file.delete();
    }
    FileGenerator fileGenerator = new FileGenerator(nodeDir, file, roleFile, config, template, pluginManager,
        version, dependencyVersions, log);
    try {
      fileGenerator.generate();
      generatedFilePaths.add(FileUtil.getCanonicalPath(file));
    }
    catch (ValidationException ex) {
      throw new GeneratorException("File validation failed " + FileUtil.getCanonicalPath(file) + " - " + ex.getMessage());
    }
    catch (Throwable ex) {
      throw new GeneratorException("Unable to generate file: " + FileUtil.getCanonicalPath(file) + "", ex);
    }
  }

}
