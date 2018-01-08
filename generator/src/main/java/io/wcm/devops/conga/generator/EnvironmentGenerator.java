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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.wcm.devops.conga.generator.export.NodeModelExport;
import io.wcm.devops.conga.generator.handlebars.HandlebarsManager;
import io.wcm.devops.conga.generator.plugins.handlebars.escaping.NoneEscapingStrategy;
import io.wcm.devops.conga.generator.plugins.multiply.NoneMultiply;
import io.wcm.devops.conga.generator.spi.MultiplyPlugin;
import io.wcm.devops.conga.generator.spi.ValidationException;
import io.wcm.devops.conga.generator.spi.context.MultiplyContext;
import io.wcm.devops.conga.generator.spi.context.ValueProviderGlobalContext;
import io.wcm.devops.conga.generator.spi.export.context.ExportNodeRoleData;
import io.wcm.devops.conga.generator.spi.export.context.GeneratedFileContext;
import io.wcm.devops.conga.generator.spi.handlebars.EscapingStrategyPlugin;
import io.wcm.devops.conga.generator.spi.handlebars.context.EscapingStrategyContext;
import io.wcm.devops.conga.generator.util.EnvironmentExpander;
import io.wcm.devops.conga.generator.util.FileUtil;
import io.wcm.devops.conga.generator.util.RoleUtil;
import io.wcm.devops.conga.generator.util.VariableMapResolver;
import io.wcm.devops.conga.generator.util.VariableObjectTreeResolver;
import io.wcm.devops.conga.generator.util.VariableStringResolver;
import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.environment.Node;
import io.wcm.devops.conga.model.environment.NodeRole;
import io.wcm.devops.conga.model.role.Role;
import io.wcm.devops.conga.model.role.RoleFile;
import io.wcm.devops.conga.model.role.RoleVariant;
import io.wcm.devops.conga.model.util.MapMerger;

/**
 * Generates file for one environment.
 */
class EnvironmentGenerator {

  private final EnvironmentGeneratorOptions options;
  private final String environmentName;
  private final Environment environment;
  private final HandlebarsManager handlebarsManager;
  private final MultiplyPlugin defaultMultiplyPlugin;
  private final Logger log;
  private final VariableStringResolver variableStringResolver;
  private final VariableMapResolver variableMapResolver;
  private final VariableObjectTreeResolver variableObjectTreeResolver;

  private final Map<String, Object> environmentContextProperties;
  private final Set<String> generatedFilePaths = new HashSet<>();

  EnvironmentGenerator(EnvironmentGeneratorOptions options, Environment environment) {
    this.options = options;
    this.environmentName = options.getEnvironmentName();
    this.environment = EnvironmentExpander.expandNodes(environment, options.getEnvironmentName());
    this.handlebarsManager = options.getHandlebarsManager();
    this.log = options.getLogger();

    ValueProviderGlobalContext valueProviderGlobalContext = new ValueProviderGlobalContext()
        .pluginContextOptions(options.getPluginContextOptions())
        .valueProviderConfig(options.getValueProviderConfig());
    this.variableStringResolver = new VariableStringResolver(valueProviderGlobalContext);
    this.variableMapResolver = new VariableMapResolver(valueProviderGlobalContext);
    this.variableObjectTreeResolver = new VariableObjectTreeResolver(valueProviderGlobalContext);

    this.defaultMultiplyPlugin = options.getPluginManager().get(NoneMultiply.NAME, MultiplyPlugin.class);
    this.environmentContextProperties = ImmutableMap.copyOf(
        ContextPropertiesBuilder.buildEnvironmentContextVariables(environmentName, this.environment, options.getVersion(),
            variableObjectTreeResolver, variableStringResolver));
  }

  public void generate() {
    log.info("");
    log.info("===== Environment '{}' =====", environmentName);

    for (Node node : environment.getNodes()) {
      generateNode(node);
    }

    log.info("");
  }

  private void generateNode(Node node) {
    if (StringUtils.isEmpty(node.getNode())) {
      throw new GeneratorException("Missing node name in " + environmentName + ".");
    }

    log.info("");
    log.info("----- Node '{}' -----", node.getNode());

    File nodeDir = FileUtil.ensureDirExistsAutocreate(new File(options.getDestDir(), node.getNode()));
    NodeModelExport exportModelGenerator = new NodeModelExport(nodeDir, node, environment, options.getModelExport(), options.getPluginManager(),
        variableStringResolver, variableMapResolver);

    for (NodeRole nodeRole : node.getRoles()) {
      // get role and resolve all inheritance relations
      Map<String, Role> resolvedRoles = RoleUtil.resolveRole(nodeRole.getRole(), environmentName + "/" + node.getNode(), options.getRoles());
      for (Map.Entry<String, Role> resolvedRole : resolvedRoles.entrySet()) {
        String roleName = resolvedRole.getKey();
        Role role = resolvedRole.getValue();

        // merge default values to config
        List<String> variants = nodeRole.getAggregatedVariants();
        Map<String, Object> mergedConfig = nodeRole.getConfig();
        if (variants.isEmpty()) {
          mergedConfig = MapMerger.merge(mergedConfig, role.getConfig());
        }
        else {
          for (String variant : variants) {
            RoleVariant roleVariant = getRoleVariant(role, variant, roleName, node);
            mergedConfig = MapMerger.merge(mergedConfig, roleVariant.getConfig());
          }
        }

        // additionally set context variables
        mergedConfig.putAll(environmentContextProperties);
        mergedConfig.putAll(ContextPropertiesBuilder.buildCurrentContextVariables(node, nodeRole));

        // collect role and tenant information for export model
        ExportNodeRoleData exportNodeRoleData = exportModelGenerator.addRole(roleName, variants, mergedConfig);

        // generate files
        List<GeneratedFileContext> allFiles = new ArrayList<>();
        for (RoleFile roleFile : role.getFiles()) {
          // generate file if no variant is required, or at least one of the given variants is defined for the node/role
          if (RoleUtil.matchesRoleFile(roleFile, variants)) {
            Template template = getHandlebarsTemplate(role, roleFile, nodeRole);
            multiplyFiles(role, roleFile, mergedConfig, nodeDir, template,
                roleName, variants, roleFile.getTemplate(), allFiles);
          }
        }
        exportNodeRoleData.files(allFiles);
      }
    }

    // save export model
    exportModelGenerator.generate();
  }

  private RoleVariant getRoleVariant(Role role, String variant, String roleName, Node node) {
    for (RoleVariant roleVariant : role.getVariants()) {
      if (StringUtils.equals(variant, roleVariant.getVariant())) {
        return roleVariant;
      }
    }
    throw new GeneratorException("Variant '" + variant + "' for role '" + roleName + "' "
        + "from " + environmentName + "/" + node.getNode() + " does not exist.");
  }

  private Template getHandlebarsTemplate(Role role, RoleFile roleFile, NodeRole nodeRole) {
    String templateFile = FileUtil.getTemplatePath(role, roleFile);
    if (StringUtils.isEmpty(templateFile)) {
      if (StringUtils.isEmpty(roleFile.getUrl())) {
        throw new GeneratorException("No template defined for file: " + FileUtil.getFileInfo(nodeRole, roleFile));
      }
      else {
        return null;
      }
    }
    try {
      Handlebars handlebars = handlebarsManager.get(getEscapingStrategy(roleFile), roleFile.getCharset());
      return handlebars.compile(templateFile);
    }
    catch (IOException ex) {
      throw new GeneratorException("Unable to compile handlebars template: " + FileUtil.getFileInfo(nodeRole, roleFile), ex);
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
    EscapingStrategyContext context = new EscapingStrategyContext()
        .pluginContextOptions(options.getPluginContextOptions());
    return options.getPluginManager().getAll(EscapingStrategyPlugin.class).stream()
        .filter(plugin -> !StringUtils.equals(plugin.getName(), NoneEscapingStrategy.NAME))
        .filter(plugin -> plugin.accepts(fileExtension, context))
        .findFirst().orElse(options.getPluginManager().get(NoneEscapingStrategy.NAME, EscapingStrategyPlugin.class))
        .getName();
  }

  private void multiplyFiles(Role role, RoleFile roleFile, Map<String, Object> config, File nodeDir, Template template,
      String roleName, List<String> roleVariantNames, String templateName, List<GeneratedFileContext> generatedFiles) {
    MultiplyPlugin multiplyPlugin = defaultMultiplyPlugin;
    if (StringUtils.isNotEmpty(roleFile.getMultiply())) {
      multiplyPlugin = options.getPluginManager().get(roleFile.getMultiply(), MultiplyPlugin.class);
    }

    MultiplyContext multiplyContext = new MultiplyContext()
        .pluginContextOptions(options.getPluginContextOptions())
        .role(role)
        .roleFile(roleFile)
        .environment(environment)
        .config(config)
        .variableStringResolver(variableStringResolver)
        .variableMapResolver(variableMapResolver);

    List<Map<String, Object>> muliplyConfigs = multiplyPlugin.multiply(multiplyContext);
    for (Map<String, Object> muliplyConfig : muliplyConfigs) {

      // resolve variables
      Map<String, Object> resolvedConfig = variableMapResolver.resolve(muliplyConfig, false);

      // replace placeholders with context variables
      String dir = variableStringResolver.resolveString(roleFile.getDir(), resolvedConfig);
      String file = variableStringResolver.resolveString(roleFile.getFile(), resolvedConfig);
      String url = variableStringResolver.resolveString(roleFile.getUrl(), resolvedConfig);

      generatedFiles.addAll(generateFile(roleFile, dir, file, url,
          resolvedConfig, nodeDir, template, roleName, roleVariantNames, templateName));
    }
  }

  private Collection<GeneratedFileContext> generateFile(RoleFile roleFile, String dir, String fileName, String url,
      Map<String, Object> config, File nodeDir, Template template,
      String roleName, List<String> roleVariantNames, String templateName) {

    String generatedFileName = fileName;
    if (StringUtils.isBlank(generatedFileName) && StringUtils.isNotBlank(url)) {
      try {
        generatedFileName = options.getUrlFileManager().getFileName(url);
      }
      catch (IOException ex) {
        throw new GeneratorException("Unable to get file name from URL: " + url, ex);
      }
    }

    File file = new File(nodeDir, dir != null ? FilenameUtils.concat(dir, generatedFileName) : generatedFileName);
    if (file.exists()) {
      file.delete();
    }

    // skip file if condition does not evaluate to a non-empty string or is "false"
    if (StringUtils.isNotEmpty(roleFile.getCondition())) {
      String condition = variableStringResolver.resolveString(roleFile.getCondition(), config);
      if (StringUtils.isBlank(condition) || StringUtils.equalsIgnoreCase(condition, "false")) {
        return ImmutableList.of();
      }
    }

    FileGenerator fileGenerator = new FileGenerator(options, roleName, roleVariantNames, templateName,
        nodeDir, file, url, roleFile, config, template, variableMapResolver);
    try {
      Collection<GeneratedFileContext> generatedFiles = fileGenerator.generate();

      // check for path duplicates
      generatedFiles.forEach(generatedFileContext -> {
        String path = generatedFileContext.getFileContext().getCanonicalPath();
        if (generatedFilePaths.contains(path)) {
          log.warn("File was generated already, check for file name clashes: " + path);
        }
        else {
          generatedFilePaths.add(path);
        }
      });

      return generatedFiles;
    }
    catch (ValidationException ex) {
      throw new GeneratorException("File validation failed " + FileUtil.getCanonicalPath(file) + " - " + ex.getMessage());
    }
    /*CHECKSTYLE:OFF*/ catch (Exception ex) { /*CHECKSTYLE:ON*/
      throw new GeneratorException("Unable to generate file: " + FileUtil.getCanonicalPath(file) + "\n" + ex.getMessage(), ex);
    }
  }

}
