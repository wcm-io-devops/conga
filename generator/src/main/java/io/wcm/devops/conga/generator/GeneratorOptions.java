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
  private Map<String, Map<String, Object>> genericPluginConfig;

  /**
   * Directories with role definitions. Filename without extension = role name.
   * @return Resource collections
   */
  public List<ResourceCollection> getRoleDirs() {
    return this.roleDirs;
  }

  /**
   * @param value Directories with role definitions. Filename without extension = role name.
   * @return this
   */
  public GeneratorOptions roleDirs(List<ResourceCollection> value) {
    this.roleDirs = value;
    return this;
  }

  /**
   * Template base directories
   * @return Resource collections
   */
  public List<ResourceCollection> getTemplateDirs() {
    return this.templateDirs;
  }

  /**
   * @param value Template base directories
   * @return this
   */
  public GeneratorOptions templateDirs(List<ResourceCollection> value) {
    this.templateDirs = value;
    return this;
  }

  /**
   * Directories with environment definitions. Filename without extension = environment name.
   * @return Resource collections
   */
  public List<ResourceCollection> getEnvironmentDirs() {
    return this.environmentDirs;
  }

  /**
   * @param value Directories with environment definitions. Filename without extension = environment name.
   * @return this
   */
  public GeneratorOptions environmentDirs(List<ResourceCollection> value) {
    this.environmentDirs = value;
    return this;
  }

  /**
   * Destination directory for generated file.
   * @return Directory
   */
  public File getDestDir() {
    return this.destDir;
  }

  /**
   * @param value Destination directory for generated file.
   * @return this
   */
  public GeneratorOptions destDir(File value) {
    this.destDir = value;
    return this;
  }

  /**
   * URL file plugin context
   * @return Context
   */
  public UrlFilePluginContext getUrlFilePluginContext() {
    return this.urlFilePluginContext;
  }

  /**
   * @param value URL file plugin context
   * @return this
   */
  public GeneratorOptions urlFilePluginContext(UrlFilePluginContext value) {
    this.urlFilePluginContext = value;
    return this;
  }

  /**
   * Set to true when the generate should delete the environment folders before generating new (default: false).
   * @return Delete before generate
   */
  public boolean isDeleteBeforeGenerate() {
    return this.deleteBeforeGenerate;
  }

  /**
   * @param value Delete before generate
   * @return this
   */
  public GeneratorOptions deleteBeforeGenerate(boolean value) {
    this.deleteBeforeGenerate = value;
    return this;
  }

  /**
   * The main version of the environment definition.
   * @return Version
   */
  public String getVersion() {
    return this.version;
  }

  /**
   * @param value Version
   * @return this
   */
  public GeneratorOptions version(String value) {
    this.version = value;
    return this;
  }

  /**
   * List of versions to include as dependency information in generated file headers,
   * e.g. the versions of the references role/template definition artifacts.
   * @return Dependency versions
   */
  public List<String> getDependencyVersions() {
    return this.dependencyVersions;
  }

  /**
   * @param value Dependency versions
   * @return this
   */
  public GeneratorOptions dependencyVersions(List<String> value) {
    this.dependencyVersions = value;
    return this;
  }

  /**
   * Settings for exporting models to files.
   * @return Model export
   */
  public ModelExport getModelExport() {
    return this.modelExport;
  }

  /**
   * @param value Model export
   * @return this
   */
  public GeneratorOptions modelExport(ModelExport value) {
    this.modelExport = value;
    return this;
  }

  /**
   * @return Configuration for value providers.
   *         The outer map uses the value provider plugin name as key, the inner map contain the config properties.
   */
  public Map<String, Map<String, Object>> getValueProviderConfig() {
    return this.valueProviderConfig;
  }

  /**
   * @param value Configuration for value providers.
   * @return this
   */
  public GeneratorOptions valueProviderConfig(Map<String, Map<String, Object>> value) {
    this.valueProviderConfig = value;
    return this;
  }

  /**
   * @return Generic plugin configuration.
   */
  public Map<String, Map<String, Object>> getGenericPluginConfig() {
    return this.genericPluginConfig;
  }

  /**
   * @param value Generic plugin configuration.
   * @return this
   */
  public GeneratorOptions genericPluginConfig(Map<String, Map<String, Object>> value) {
    this.genericPluginConfig = value;
    return this;
  }

}
