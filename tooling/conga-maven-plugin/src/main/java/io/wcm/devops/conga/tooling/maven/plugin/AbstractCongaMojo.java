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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.sling.commons.osgi.ManifestHeader;
import org.apache.sling.commons.osgi.ManifestHeader.NameValuePair;

import com.google.common.collect.ImmutableList;

import io.wcm.devops.conga.generator.export.ModelExport;
import io.wcm.devops.conga.resource.ResourceCollection;
import io.wcm.devops.conga.resource.ResourceLoader;

/**
 * Common features for all Mojos.
 */
abstract class AbstractCongaMojo extends AbstractMojo {

  /**
   * Source path with templates.
   */
  @Parameter(defaultValue = "${basedir}/src/main/templates")
  private String templateDir;

  /**
   * Source path with role definitions.
   */
  @Parameter(defaultValue = "${basedir}/src/main/roles")
  private String roleDir;

  /**
   * Target path for the generated configuration files.
   */
  @Parameter(defaultValue = "${project.build.directory}/configuration")
  private String target;

  /**
   * Source path with environment definitions.
   */
  @Parameter(defaultValue = "${basedir}/src/main/environments")
  private String environmentDir;

  /**
   * List for plugins for exporting model data for nodes.
   * You can specify multiple plugins separated by ",".
   * To disable export of model data set to "none".
   */
  @Parameter(defaultValue = "yaml")
  private String modelExportNode;

  /**
   * Configuration for value providers.
   * This uses the same syntax as OSGi manifest headers - example:
   * <code>valueProviderPluginName1;param1=value1;param2=value2,valueProviderPluginName2;param3=value3</code>
   */
  @Parameter
  private String valueProvider;

  protected ResourceCollection getTemplateDir() {
    return getResourceLoader().getResourceCollection(ResourceLoader.FILE_PREFIX + templateDir);
  }

  protected ResourceCollection getRoleDir() {
    return getResourceLoader().getResourceCollection(ResourceLoader.FILE_PREFIX + roleDir);
  }

  protected ResourceCollection getEnvironmentDir() {
    return getResourceLoader().getResourceCollection(ResourceLoader.FILE_PREFIX + environmentDir);
  }

  protected File getTargetDir() {
    return new File(target);
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
    if (StringUtils.isEmpty(this.valueProvider)) {
      return Collections.emptyMap();
    }
    Map<String, Map<String, Object>> valueProviderConfig = new HashMap<>();
    ManifestHeader header = ManifestHeader.parse(this.valueProvider);
    for (ManifestHeader.Entry entry : header.getEntries()) {
      Map<String, Object> config = new HashMap<>();
      for (NameValuePair nameValue : entry.getAttributes()) {
        config.put(nameValue.getName(), nameValue.getValue());
      }
      valueProviderConfig.put(entry.getValue(), Collections.unmodifiableMap(config));
    }
    return Collections.unmodifiableMap(valueProviderConfig);
  }

  protected abstract ResourceLoader getResourceLoader();

}
