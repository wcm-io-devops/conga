/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2018 wcm.io
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
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import io.wcm.devops.conga.generator.export.ModelExport;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.model.role.Role;

/**
 * Options for environment generator.
 */
class EnvironmentGeneratorOptions {

  private Map<String, Role> roles;
  private String environmentName;
  private File destDir;
  private PluginManager pluginManager;
  private String version;
  private ModelExport modelExport;
  private Map<String, Map<String, Object>> valueProviderConfig;
  private Map<String, Map<String, Object>> genericPluginConfig;
  private Object urlFilePluginContainerContext;
  private List<String> containerDependencyUrls;
  private Logger logger;

  public Map<String, Role> getRoles() {
    return this.roles;
  }

  public EnvironmentGeneratorOptions roles(Map<String, Role> value) {
    this.roles = value;
    return this;
  }

  public String getEnvironmentName() {
    return this.environmentName;
  }

  public EnvironmentGeneratorOptions environmentName(String value) {
    this.environmentName = value;
    return this;
  }

  public File getDestDir() {
    return this.destDir;
  }

  public EnvironmentGeneratorOptions destDir(File value) {
    this.destDir = value;
    return this;
  }

  public PluginManager getPluginManager() {
    return this.pluginManager;
  }

  public EnvironmentGeneratorOptions pluginManager(PluginManager value) {
    this.pluginManager = value;
    return this;
  }

  public String getVersion() {
    return this.version;
  }

  public EnvironmentGeneratorOptions version(String value) {
    this.version = value;
    return this;
  }

  public ModelExport getModelExport() {
    return this.modelExport;
  }

  public EnvironmentGeneratorOptions modelExport(ModelExport value) {
    this.modelExport = value;
    return this;
  }

  public Map<String, Map<String, Object>> getValueProviderConfig() {
    return this.valueProviderConfig;
  }

  public EnvironmentGeneratorOptions valueProviderConfig(Map<String, Map<String, Object>> value) {
    this.valueProviderConfig = value;
    return this;
  }

  public Map<String, Map<String, Object>> getGenericPluginConfig() {
    return this.genericPluginConfig;
  }

  public EnvironmentGeneratorOptions genericPluginConfig(Map<String, Map<String, Object>> value) {
    this.genericPluginConfig = value;
    return this;
  }

  public Object getUrlFilePluginContainerContext() {
    return this.urlFilePluginContainerContext;
  }

  public EnvironmentGeneratorOptions urlFilePluginContainerContext(Object value) {
    this.urlFilePluginContainerContext = value;
    return this;
  }

  public List<String> getContainerDependencyUrls() {
    return this.containerDependencyUrls;
  }

  public EnvironmentGeneratorOptions containerDependencyUrls(List<String> value) {
    this.containerDependencyUrls = value;
    return this;
  }

  public Logger getLogger() {
    return this.logger;
  }

  public EnvironmentGeneratorOptions logger(Logger value) {
    this.logger = value;
    return this;
  }

}
