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
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.wcm.devops.conga.generator.export.NodeModelExport;
import io.wcm.devops.conga.generator.handlebars.HandlebarsManager;
import io.wcm.devops.conga.generator.plugins.handlebars.escaping.NoneEscapingStrategy;
import io.wcm.devops.conga.generator.plugins.multiply.NoneMultiply;
import io.wcm.devops.conga.generator.spi.MultiplyPlugin;
import io.wcm.devops.conga.generator.spi.ValidationException;
import io.wcm.devops.conga.generator.spi.context.MultiplyContext;
import io.wcm.devops.conga.generator.spi.context.PluginContextOptions;
import io.wcm.devops.conga.generator.spi.context.UrlFilePluginContext;
import io.wcm.devops.conga.generator.spi.context.ValueProviderGlobalContext;
import io.wcm.devops.conga.generator.spi.export.context.ExportNodeRoleData;
import io.wcm.devops.conga.generator.spi.export.context.GeneratedFileContext;
import io.wcm.devops.conga.generator.spi.handlebars.EscapingStrategyPlugin;
import io.wcm.devops.conga.generator.spi.handlebars.context.EscapingStrategyContext;
import io.wcm.devops.conga.generator.spi.yaml.YamlRepresentPlugin;
import io.wcm.devops.conga.generator.spi.yaml.context.YamlRepresentContext;
import io.wcm.devops.conga.generator.spi.yaml.context.YamlRepresenter;
import io.wcm.devops.conga.generator.util.EnvironmentExpander;
import io.wcm.devops.conga.generator.util.FileUtil;
import io.wcm.devops.conga.generator.util.RoleUtil;
import io.wcm.devops.conga.generator.util.VariableMapResolver;
import io.wcm.devops.conga.generator.util.VariableObjectTreeResolver;
import io.wcm.devops.conga.generator.util.VariableStringResolver;
import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.environment.Node;
import io.wcm.devops.conga.model.environment.NodeRole;
import io.wcm.devops.conga.model.reader.RoleReader;
import io.wcm.devops.conga.model.role.Role;
import io.wcm.devops.conga.model.role.RoleFile;
import io.wcm.devops.conga.model.role.RoleVariant;
import io.wcm.devops.conga.model.util.MapMerger;
import io.wcm.devops.conga.resource.ResourceCollection;
import io.wcm.devops.conga.resource.ResourceLoader;

/**
 * Generates file for one environment.
 */
final class EnvironmentGenerator {

  private final GeneratorOptions options;
  private final String environmentName;
  private final Environment environment;
  private final File destDir;
  private final PluginContextOptions pluginContextOptions;
  private final HandlebarsManager handlebarsManager;
  private final UrlFilePluginContext urlFilePluginContext;
  private final UrlFileManager urlFileManager;
  private final MultiplyPlugin defaultMultiplyPlugin;
  private final Logger log;
  private final VariableStringResolver variableStringResolver;
  private final VariableMapResolver variableMapResolver;
  private final Collection<String> dependencyVersions;
  private final Set<String> sensitiveConfigParameters = new HashSet<>();

  private final Map<String, Role> roles;
  private final Map<String, Object> environmentContextProperties;
  private final Set<String> generatedFilePaths = new HashSet<>();
  private final YamlRepresenter yamlRepresenter;

  EnvironmentGenerator(String environmentName, Environment environment, File destDir,
      GeneratorOptions options) {
    this.options = options;
    this.environmentName = environmentName;
    this.environment = EnvironmentExpander.expandNodes(environment, environmentName);
    this.destDir = destDir;
    this.log = options.getLogger();

    this.pluginContextOptions = new PluginContextOptions()
        .pluginManager(options.getPluginManager())
        .valueProviderConfig(options.getValueProviderConfig())
        .genericPluginConfig(mergePluginConfig(environment.getPluginConfig(), options.getGenericPluginConfig()))
        .containerContext(options.getContainerContext())
        .logger(this.log);

    // prepare variable resolvers
    ValueProviderGlobalContext valueProviderGlobalContext = new ValueProviderGlobalContext()
        .pluginContextOptions(this.pluginContextOptions);
    this.variableMapResolver = new VariableMapResolver(valueProviderGlobalContext);
    this.variableStringResolver = new VariableStringResolver(valueProviderGlobalContext, variableMapResolver);
    VariableObjectTreeResolver variableObjectTreeResolver = new VariableObjectTreeResolver(valueProviderGlobalContext);

    // build resource loaded based on combined dependency lists of environment and container
    List<URL> combindedClasspathUrls = ResourceLoaderUtil.getEnvironmentClasspathUrls(environment.getDependencies(), this.variableStringResolver, options);
    ClassLoader resourceClassLoader = ResourceLoaderUtil.buildClassLoader(combindedClasspathUrls);
    ResourceLoader resourceLoader = new ResourceLoader(resourceClassLoader);

    // prepare template and role directories
    List<ResourceCollection> templateDirs = List.of(
        resourceLoader.getResourceCollection(ResourceLoader.FILE_PREFIX + options.getTemplateDir()),
        resourceLoader.getResourceCollection(ResourceLoader.CLASSPATH_PREFIX + GeneratorOptions.CLASSPATH_TEMPLATES_DIR));
    List<ResourceCollection> roleDirs = List.of(
        resourceLoader.getResourceCollection(ResourceLoader.FILE_PREFIX + options.getRoleDir()),
        resourceLoader.getResourceCollection(ResourceLoader.CLASSPATH_PREFIX + GeneratorOptions.CLASSPATH_ROLES_DIR));

    this.roles = ResourceLoaderUtil.readModels(roleDirs, new RoleReader());

    // collect sensitive configuration parameter names from all roles
    this.roles.values().forEach(role -> sensitiveConfigParameters.addAll(role.getSensitiveConfigParameters()));

    this.urlFilePluginContext = new UrlFilePluginContext()
        .pluginContextOptions(pluginContextOptions)
        .baseDir(options.getBaseDir())
        .resourceClassLoader(resourceClassLoader)
        .environment(environment);
    this.urlFileManager = new UrlFileManager(options.getPluginManager(), this.urlFilePluginContext);

    this.handlebarsManager = new HandlebarsManager(templateDirs, this.pluginContextOptions);

    this.defaultMultiplyPlugin = options.getPluginManager().get(NoneMultiply.NAME, MultiplyPlugin.class);
    this.environmentContextProperties = Collections.unmodifiableMap(
        ContextPropertiesBuilder.buildEnvironmentContextVariables(environmentName, this.environment, options.getVersion(),
            variableObjectTreeResolver, variableStringResolver));

    this.dependencyVersions = options.getDependencyVersionBuilder() != null ? options.getDependencyVersionBuilder().apply(environment) : List.of();

    // prepare YAML representer
    yamlRepresenter = new YamlRepresenter();
    options.getPluginManager().getAll(YamlRepresentPlugin.class).forEach(plugin -> {
      YamlRepresentContext context = new YamlRepresentContext()
          .pluginContextOptions(pluginContextOptions)
          .yamlRepresenter(yamlRepresenter);
      plugin.register(context);
    });
  }

  /**
   * Generate files for environment.
   * @param nodeNames Node names to generate. If none specified all nodes are generated.
   */
  public void generate(String[] nodeNames) {
    log.info("");
    log.info("===== Environment '{}' =====", environmentName);

    Set<String> nodeNamesIndex = ArrayUtils.isEmpty(nodeNames) ? Collections.emptySet() : Set.of(nodeNames);
    for (Node node : environment.getNodes()) {
      if (isSelectedNode(node, nodeNamesIndex)) {
        generateNode(node);
      }
    }

    log.info("");
  }

  private boolean isSelectedNode(Node node, Set<String> nodeNames) {
    if (nodeNames.isEmpty()) {
      return true;
    }
    if (StringUtils.isNotBlank(node.getNode()) && nodeNames.contains(node.getNode())) {
      return true;
    }
    return CollectionUtils.containsAny(node.getNodes(), nodeNames);
  }

  private void generateNode(Node node) {
    if (StringUtils.isEmpty(node.getNode())) {
      throw new GeneratorException("Missing node name in " + environmentName + ".");
    }

    log.info("");
    log.info("----- Node '{}' -----", node.getNode());

    File nodeDir = FileUtil.ensureDirExistsAutocreate(new File(destDir, node.getNode()));
    try {
      this.urlFilePluginContext.baseNodeDir(nodeDir);

      NodeModelExport exportModelGenerator = new NodeModelExport(nodeDir, node, environment, options.getModelExport(),
          variableStringResolver, variableMapResolver, options.getContainerVersionInfo(), pluginContextOptions,
          sensitiveConfigParameters, yamlRepresenter);

      for (NodeRole nodeRole : node.getRoles()) {
        // get role and resolve all inheritance relations
        Map<String, Role> resolvedRoles = RoleUtil.resolveRole(nodeRole.getRole(), environmentName + "/" + node.getNode(), roles);
        for (Map.Entry<String, Role> resolvedRole : resolvedRoles.entrySet()) {
          String roleName = resolvedRole.getKey();
          Role role = resolvedRole.getValue();
          List<String> variants = nodeRole.getAggregatedVariants();

          // collect default config from role and it's variant.
          // default config in variants has higher precedence than config in the role itself
          // variants listed first have higher precedence than variants listed last
          Map<String, Object> roleDefaultConfig = new HashMap<>();
          for (String variant : variants) {
            RoleVariant roleVariant = getRoleVariant(role, variant, roleName, node);
            roleDefaultConfig = MapMerger.merge(roleDefaultConfig, roleVariant.getConfig());
          }
          roleDefaultConfig = MapMerger.merge(roleDefaultConfig, role.getConfig());

          // merge default values to config
          Map<String, Object> mergedConfig = nodeRole.getConfig();
          mergedConfig = MapMerger.merge(mergedConfig, roleDefaultConfig);

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

          // filter out result files probably deleted by other file definitions
          allFiles = allFiles.stream()
              .filter(generatedFile -> generatedFile.getFileContext().getFile().exists())
              .collect(Collectors.toList());

          exportNodeRoleData.files(allFiles);
        }
      }

      // save export model
      exportModelGenerator.generate();
    }
    finally {
      this.urlFilePluginContext.baseNodeDir(null);
    }
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
      if (StringUtils.isEmpty(roleFile.getUrl()) && StringUtils.isEmpty(roleFile.getSymlinkTarget())) {
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
        .pluginContextOptions(this.pluginContextOptions);
    return options.getPluginManager().getAll(EscapingStrategyPlugin.class).stream()
        .filter(plugin -> !StringUtils.equals(plugin.getName(), NoneEscapingStrategy.NAME))
        .filter(plugin -> plugin.accepts(fileExtension, context))
        .findFirst().orElse(options.getPluginManager().get(NoneEscapingStrategy.NAME, EscapingStrategyPlugin.class))
        .getName();
  }

  @SuppressWarnings("java:S107") // allow many parameters
  private void multiplyFiles(Role role, RoleFile roleFile, Map<String, Object> config, File nodeDir, Template template,
      String roleName, List<String> roleVariantNames, String templateName, List<GeneratedFileContext> generatedFiles) {
    MultiplyPlugin multiplyPlugin = defaultMultiplyPlugin;
    if (StringUtils.isNotEmpty(roleFile.getMultiply())) {
      multiplyPlugin = options.getPluginManager().get(roleFile.getMultiply(), MultiplyPlugin.class);
    }

    MultiplyContext multiplyContext = new MultiplyContext()
        .pluginContextOptions(this.pluginContextOptions)
        .role(role)
        .roleFile(roleFile)
        .environment(environment)
        .config(config)
        .variableStringResolver(variableStringResolver)
        .variableMapResolver(variableMapResolver);

    List<Map<String, Object>> muliplyConfigs = multiplyPlugin.multiply(multiplyContext);
    int index = 0;
    for (Map<String, Object> muliplyConfig : muliplyConfigs) {

      // resolve variables
      Map<String, Object> resolvedConfig = variableMapResolver.resolve(muliplyConfig, false);
      resolvedConfig.put(ContextProperties.MULTIPLY_INDEX, index);

      // skip file if condition does not evaluate to a non-empty string or is "false"
      boolean skip = false;
      if (StringUtils.isNotEmpty(roleFile.getCondition())) {
        String condition = variableStringResolver.resolveString(roleFile.getCondition(), resolvedConfig);
        skip = StringUtils.isBlank(condition) || StringUtils.equalsIgnoreCase(condition, "false");
      }

      if (!skip) {
        // replace placeholders with context variables
        String dir = variableStringResolver.resolveString(roleFile.getDir(), resolvedConfig);
        String file = variableStringResolver.resolveString(roleFile.getFile(), resolvedConfig);
        String url = variableStringResolver.resolveString(roleFile.getUrl(), resolvedConfig);
        String symlinkTarget = variableStringResolver.resolveString(roleFile.getSymlinkTarget(), resolvedConfig);

        generatedFiles.addAll(generateFile(roleFile, dir, file, url, symlinkTarget,
            resolvedConfig, nodeDir, template, roleName, roleVariantNames, templateName));

        index++;
      }
    }
  }

  @SuppressWarnings({
      "PMD.PreserveStackTrace",
      "java:S107" // allow many parameters
  })
  @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
  private Collection<GeneratedFileContext> generateFile(RoleFile roleFile, String dir,
      String fileName, String url, String symlinkTarget,
      Map<String, Object> config, File nodeDir, Template template,
      String roleName, List<String> roleVariantNames, String templateName) {

    String generatedFileName = fileName;
    if (StringUtils.isBlank(generatedFileName) && StringUtils.isNotBlank(url)) {
      try {
        generatedFileName = urlFileManager.getFileName(url);
      }
      catch (IOException ex) {
        throw new GeneratorException("Unable to get file name from URL: " + url, ex);
      }
    }

    File file = new File(nodeDir, dir != null ? FilenameUtils.concat(dir, generatedFileName) : generatedFileName);
    if (file.exists()) {
      try {
        Files.delete(file.toPath());
      }
      catch (IOException ex) {
        throw new GeneratorException("Unable to delete: " + FileUtil.getCanonicalPath(file), ex);
      }
    }

    FileGenerator fileGenerator = new FileGenerator(options, environmentName,
        roleName, roleVariantNames, templateName,
        nodeDir, file, url, symlinkTarget,
        roleFile, config, template,
        variableMapResolver, urlFileManager, pluginContextOptions, dependencyVersions);
    try {
      Collection<GeneratedFileContext> generatedFiles = fileGenerator.generate();

      // check for path duplicates
      generatedFiles.forEach(generatedFileContext -> {
        String path = generatedFileContext.getFileContext().getCanonicalPath();
        if (generatedFilePaths.contains(path)) {
          log.warn("File was generated already, check for file name clashes: {}", path);
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

  @SuppressWarnings("unchecked")
  private Map<String, Map<String, Object>> mergePluginConfig(Map<String, Map<String, Object>> map1, Map<String, Map<String, Object>> map2) {
    return MapMerger.merge((Map)map1, (Map)map2);
  }

}
