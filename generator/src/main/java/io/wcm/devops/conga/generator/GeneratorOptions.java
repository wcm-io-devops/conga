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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.wcm.devops.conga.generator.export.ModelExport;

/**
 * Options for generator.
 */
public class GeneratorOptions {

  private File baseDir;
  private String roleDir;
  private String templateDir;
  private String environmentDir;
  private File destDir;
  private boolean deleteBeforeGenerate;
  private String version;
  private ModelExport modelExport;
  private Map<String, Map<String, Object>> valueProviderConfig;
  private Map<String, Map<String, Object>> genericPluginConfig;
  private Object urlFilePluginContainerContext;
  private List<String> containerDependencyUrls;
  private Logger logger = LoggerFactory.getLogger(Generator.class);

  /**
   * @return Base directory for resolving relative file references
   */
  public File getBaseDir() {
    return this.baseDir;
  }

  /**
   * @param value Base directory for resolving relative file references
   * @return this
   */
  public GeneratorOptions baseDir(File value) {
    this.baseDir = value;
    return this;
  }

  /**
   * Directory with role definitions. Filename without extension = role name.
   * @return Directory path
   */
  public String getRoleDir() {
    return this.roleDir;
  }

  /**
   * @param value Directory with role definitions. Filename without extension = role name.
   * @return this
   */
  public GeneratorOptions roleDirs(String value) {
    this.roleDir = value;
    return this;
  }

  /**
   * Template base directory
   * @return Directory path
   */
  public String getTemplateDir() {
    return this.templateDir;
  }

  /**
   * @param value Template base directory
   * @return this
   */
  public GeneratorOptions templateDirs(String value) {
    this.templateDir = value;
    return this;
  }

  /**
   * Directory with environment definitions. Filename without extension = environment name.
   * @return Directory path
   */
  public String getEnvironmentDir() {
    return this.environmentDir;
  }

  /**
   * @param value Directory with environment definitions. Filename without extension = environment name.
   * @return this
   */
  public GeneratorOptions environmentDirs(String value) {
    this.environmentDir = value;
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

  /**
   * Container-specific context object for URL file plugin context
   * @return Context
   */
  public Object getUrlFilePluginContainerContext() {
    return this.urlFilePluginContainerContext;
  }

  /**
   * @param value Container-specific context object for URL file plugin context
   * @return this
   */
  public GeneratorOptions urlFilePluginContainerContext(Object value) {
    this.urlFilePluginContainerContext = value;
    return this;
  }

  /**
   * @return List of URLs to CONGA artifact dependencies defined in the container.
   */
  public List<String> getContainerDependencyUrls() {
    return this.containerDependencyUrls;
  }

  /**
   * @param value List of URLs to CONGA artifact dependencies defined in the container.
   * @return this
   */
  public GeneratorOptions containerDependencyUrls(List<String> value) {
    this.containerDependencyUrls = value;
    return this;
  }

  /**
   * @return Logger
   */
  public Logger getLogger() {
    return this.logger;
  }

  /**
   * @param value Logger
   * @return this
   */
  public GeneratorOptions logger(Logger value) {
    this.logger = value;
    return this;
  }

}
