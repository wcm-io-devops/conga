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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.wcm.devops.conga.generator.export.ModelExport;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.model.environment.Environment;

/**
 * Options for generator.
 */
public final class GeneratorOptions {

  /**
   * Prefix for all CONGA resources in classpath.
   */
  public static final String CLASSPATH_PREFIX = "CONGA-INF/";

  /**
   * Classpath directory in JAR file for roles
   */
  public static final String CLASSPATH_ROLES_DIR = CLASSPATH_PREFIX + "roles";

  /**
   * Classpath directory in JAR file for roles
   */
  public static final String CLASSPATH_TEMPLATES_DIR = CLASSPATH_PREFIX + "templates";

  /**
   * Classpath directory in JAR file for roles
   */
  public static final String CLASSPATH_ENVIRONMENTS_DIR = CLASSPATH_PREFIX + "environments";

  private File baseDir;
  private File roleDir;
  private File templateDir;
  private File environmentDir;
  private File destDir;
  private boolean deleteBeforeGenerate;
  private String version;
  private boolean allowSymlinks = true;
  private ModelExport modelExport;
  private Map<String, Map<String, Object>> valueProviderConfig;
  private Map<String, Map<String, Object>> genericPluginConfig;
  private Object containerContext;
  private List<URL> containerClasspathUrls = new ArrayList<>();
  private PluginManager pluginManager;
  private Function<Environment, Collection<String>> dependencyVersionBuilder;
  private Map<String, String> containerVersionInfo;
  @SuppressWarnings("java:S3416") // uses logger for Generator by intention
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
   * @return Directory
   */
  public File getRoleDir() {
    return this.roleDir;
  }

  /**
   * @param value Directory with role definitions. Filename without extension = role name.
   * @return this
   */
  public GeneratorOptions roleDir(File value) {
    this.roleDir = value;
    return this;
  }

  /**
   * Template base directory
   * @return Directory
   */
  public File getTemplateDir() {
    return this.templateDir;
  }

  /**
   * @param value Template base directory
   * @return this
   */
  public GeneratorOptions templateDir(File value) {
    this.templateDir = value;
    return this;
  }

  /**
   * Directory with environment definitions. Filename without extension = environment name.
   * @return Directory
   */
  public File getEnvironmentDir() {
    return this.environmentDir;
  }

  /**
   * @param value Directory with environment definitions. Filename without extension = environment name.
   * @return this
   */
  public GeneratorOptions environmentDir(File value) {
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
   * Is it allowed to create symlinks instead of copying files if they are local files.
   * @return Allow symlinks
   */
  public boolean isAllowSymlinks() {
    return this.allowSymlinks;
  }

  /**
   * @param value Allow symlinks
   * @return this
   */
  public GeneratorOptions setAllowSymlinks(boolean value) {
    this.allowSymlinks = value;
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
   * Container-specific context object
   * @return Context
   */
  public Object getContainerContext() {
    return this.containerContext;
  }

  /**
   * @param value Container-specific context object
   * @return this
   */
  public GeneratorOptions containerContext(Object value) {
    this.containerContext = value;
    return this;
  }

  /**
   * @return List of classpath URLs to CONGA artifact dependencies and container classpath elements.
   */
  public List<URL> getContainerClasspathUrls() {
    return this.containerClasspathUrls;
  }

  /**
   * @param value List of URLs to CONGA artifact dependencies defined in the container.
   * @return this
   */
  public GeneratorOptions containerClasspathUrls(List<URL> value) {
    this.containerClasspathUrls = value;
    return this;
  }

  /**
   * @return Plugin manager
   */
  public PluginManager getPluginManager() {
    return this.pluginManager;
  }

  /**
   * @param value Plugin manager
   * @return this
   */
  public GeneratorOptions pluginManager(PluginManager value) {
    this.pluginManager = value;
    return this;
  }

  /**
   * @return Function to build a list of dependency versions for a given environment
   */
  public Function<Environment, Collection<String>> getDependencyVersionBuilder() {
    return this.dependencyVersionBuilder;
  }

  /**
   * @param value Function to build a list of dependency versions for a given environment
   * @return this
   */
  public GeneratorOptions dependencyVersionBuilder(Function<Environment, Collection<String>> value) {
    this.dependencyVersionBuilder = value;
    return this;
  }

  /**
   * @return Version information from container, e.g. configured Maven plugin versions
   */
  public Map<String, String> getContainerVersionInfo() {
    return this.containerVersionInfo;
  }

  /**
   * @param value Version information from container, e.g. configured Maven plugin versions
   * @return this
   */
  public GeneratorOptions containerVersionInfo(Map<String, String> value) {
    this.containerVersionInfo = value;
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
