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
package io.wcm.devops.conga.tooling.maven.plugin;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.wcm.devops.conga.generator.export.ModelExport;
import io.wcm.devops.conga.tooling.maven.plugin.util.PluginConfigUtil;

/**
 * Common features for all Mojos.
 */
abstract class AbstractCongaMojo extends AbstractMojo {

  /**
   * Source path with templates.
   */
  @Parameter(defaultValue = "${basedir}/src/main/templates")
  private File templateDir;

  /**
   * Source path with role definitions.
   */
  @Parameter(defaultValue = "${basedir}/src/main/roles")
  private File roleDir;

  /**
   * Target path for the generated configuration files.
   */
  @Parameter(defaultValue = "${project.build.directory}/configuration")
  private File target;

  /**
   * Source path with environment definitions.
   */
  @Parameter(defaultValue = "${basedir}/src/main/environments")
  private File environmentDir;

  /**
   * List for plugins for exporting model data for nodes.
   * You can specify multiple plugins separated by ",".
   * To disable export of model data set to "none".
   */
  @Parameter(defaultValue = "yaml")
  private String modelExportNode;

  /**
   * Configuration for value providers.
   * <p>
   * This uses the same syntax as OSGi manifest headers - example:
   * </p>
   *
   * <pre>
   * valueProviderPluginName1;param1=value1;param2=value2,
   * valueProviderPluginName2;param3=value3
   * </pre>
   * <p>
   * If you want to define multiple value providers of the same type, you can use an arbitrary value provider name, and
   * specify the plugin name with the optional <code>_plugin_</code> parameter - example:
   * </p>
   *
   * <pre>
   * valueProvider1;_plugin_=valueProviderPluginName1,param1=value1;param2=value2,
   * valueProvider2;_plugin_=valueProviderPluginName1,param3=value3
   * </pre>
   */
  @Parameter
  private String valueProvider;

  /**
   * Plugin-specific configuration. This holds configuration for CONGA plugins that are not part of the built-in set of
   * CONGA plugins (e.g. configuration for the CONGA AEM Plugin).
   * <p>
   * This uses the same syntax as OSGi manifest headers - example:
   * </p>
   *
   * <pre>
   * pluginName1;param1=value1;param2=value2,
   * pluginName2;param3=value3
   * </pre>
   */
  @Parameter
  private String pluginConfig;

  /**
   * Allows to define custom artifact type to extension mappings for resolving dependencies from artifact coordinates
   * where it is not fully clear if the an extension is really the extension or a artifact type identifier.
   */
  @Parameter
  private Map<String,String> artifactTypeMappings;

  private static final Map<String, String> DEFAULT_ARTIFACT_TYPE_MAPPINGS = ImmutableMap.of(
      "bundle", "jar",
      "content-package", "zip");

  protected File getTemplateDir() {
    return templateDir;
  }

  protected File getRoleDir() {
    return roleDir;
  }

  protected File getEnvironmentDir() {
    return environmentDir;
  }

  protected File getTargetDir() {
    return target;
  }

  protected ModelExport getModelExport() {
    ModelExport modelExport = new ModelExport();

    String[] nodeExportPlugins = StringUtils.split(this.modelExportNode, ",");
    if (nodeExportPlugins != null) {
      modelExport.setNode(ImmutableList.copyOf(nodeExportPlugins));
    }

    return modelExport;
  }

  protected Map<String, Map<String, Object>> getValueProviderConfig() {
    return PluginConfigUtil.getConfigMap(this.valueProvider);
  }

  protected Map<String, Map<String, Object>> getPluginConfig() {
    return PluginConfigUtil.getConfigMap(this.pluginConfig);
  }

  protected Map<String, String> getArtifactTypeMappings() {
    Map<String, String> mappings = this.artifactTypeMappings;
    if (mappings == null) {
      mappings = DEFAULT_ARTIFACT_TYPE_MAPPINGS;
    }
    return mappings;
  }

}
