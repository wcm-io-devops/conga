/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 wcm.io
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

import io.wcm.devops.conga.generator.export.ModelExport;
import io.wcm.devops.conga.generator.spi.context.UrlFilePluginContext;
import io.wcm.devops.conga.resource.ResourceCollection;

/**
 * Options for generator.
 */
public class GeneratorOptions {

  private List<ResourceCollection> roleDirs;
  private List<ResourceCollection> templateDirs;
  private List<ResourceCollection> environmentDirs;
  private File destDir;
  private UrlFilePluginContext urlFilePluginContext;
  private boolean deleteBeforeGenerate;
  private String version;
  private List<String> dependencyVersions;
  private ModelExport modelExport;
  private Map<String, Map<String, Object>> valueProviderConfig;

  /**
   * Directories with role definitions. Filename without extension = role name.
   * @return Resource collections
   */
  public List<ResourceCollection> getRoleDirs() {
    return this.roleDirs;
  }

  public void setRoleDirs(List<ResourceCollection> roleDirs) {
    this.roleDirs = roleDirs;
  }

  /**
   * Template base directories
   * @return Resource collections
   */
  public List<ResourceCollection> getTemplateDirs() {
    return this.templateDirs;
  }

  public void setTemplateDirs(List<ResourceCollection> templateDirs) {
    this.templateDirs = templateDirs;
  }

  /**
   * Directories with environment definitions. Filename without extension = environment name.
   * @return Resource collections
   */
  public List<ResourceCollection> getEnvironmentDirs() {
    return this.environmentDirs;
  }

  public void setEnvironmentDirs(List<ResourceCollection> environmentDirs) {
    this.environmentDirs = environmentDirs;
  }

  /**
   * Destination directory for generated file.
   * @return Directory
   */
  public File getDestDir() {
    return this.destDir;
  }

  public void setDestDir(File destDir) {
    this.destDir = destDir;
  }

  /**
   * URL file plugin context
   * @return Context
   */
  public UrlFilePluginContext getUrlFilePluginContext() {
    return this.urlFilePluginContext;
  }

  public void setUrlFilePluginContext(UrlFilePluginContext urlFilePluginContext) {
    this.urlFilePluginContext = urlFilePluginContext;
  }

  /**
   * deleteBeforeGenerate Set to true when the generate should delete the environment folders before generating new
   * (default: false).
   * @return Delete before generate
   */
  public boolean isDeleteBeforeGenerate() {
    return this.deleteBeforeGenerate;
  }

  public void setDeleteBeforeGenerate(boolean deleteBeforeGenerate) {
    this.deleteBeforeGenerate = deleteBeforeGenerate;
  }

  /**
   * The main version of the environment definition.
   * @return Version
   */
  public String getVersion() {
    return this.version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * List of versions to include as dependency information in generated file headers,
   * e.g. the versions of the references role/template definition artifacts.
   * @return Dependency versions
   */
  public List<String> getDependencyVersions() {
    return this.dependencyVersions;
  }

  public void setDependencyVersions(List<String> dependencyVersions) {
    this.dependencyVersions = dependencyVersions;
  }

  /**
   * Settings for exporting models to files.
   * @return Model export
   */
  public ModelExport getModelExport() {
    return this.modelExport;
  }

  public void setModelExport(ModelExport modelExport) {
    this.modelExport = modelExport;
  }

  /**
   * @return Configuration for value providers.
   *         The outer map uses the value provider plugin name as key, the inner map contain the config properties.
   */
  public Map<String, Map<String, Object>> getValueProviderConfig() {
    return this.valueProviderConfig;
  }


  public void setValueProviderConfig(Map<String, Map<String, Object>> valueProviderConfig) {
    this.valueProviderConfig = valueProviderConfig;
  }

}
