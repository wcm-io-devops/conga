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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import io.wcm.devops.conga.generator.export.ModelExport;
import io.wcm.devops.conga.generator.handlebars.HandlebarsManager;
import io.wcm.devops.conga.generator.util.ConfigInheritanceResolver;
import io.wcm.devops.conga.generator.util.FileUtil;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;
import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.reader.EnvironmentReader;
import io.wcm.devops.conga.model.reader.ModelReader;
import io.wcm.devops.conga.model.reader.RoleReader;
import io.wcm.devops.conga.model.role.Role;
import io.wcm.devops.conga.resource.Resource;
import io.wcm.devops.conga.resource.ResourceCollection;

/**
 * Main entry point for CONGA generator.
 */
public final class Generator {

  private final Map<String, Role> roles;
  private final Map<String, Environment> environments;
  private final File destDir;
  private final PluginManager pluginManager;
  private final HandlebarsManager handlebarsManager;
  private final UrlFileManager urlFileManager;
  private final boolean deleteBeforeGenerate;
  private final String version;
  private final List<String> dependencyVersions;
  private final ModelExport modelExport;
  private Logger log = LoggerFactory.getLogger(getClass());

  /**
   * @param options Generator options
   */
  public Generator(GeneratorOptions options) {
    this.pluginManager = new PluginManagerImpl();
    this.roles = readModels(options.getRoleDirs(), new RoleReader());
    this.environments = readModels(options.getEnvironmentDirs(), new EnvironmentReader());
    this.destDir = FileUtil.ensureDirExistsAutocreate(options.getDestDir());
    this.handlebarsManager = new HandlebarsManager(options.getTemplateDirs(), this.pluginManager);
    this.urlFileManager = new UrlFileManager(this.pluginManager, options.getUrlFilePluginContext());
    this.deleteBeforeGenerate = options.isDeleteBeforeGenerate();
    this.version = options.getVersion();
    this.dependencyVersions = options.getDependencyVersions();
    this.modelExport = options.getModelExport();
  }

  /**
   * @param logger Logger to use for generation process logging.
   */
  public void setLogger(Logger logger) {
    log = logger;
  }

  private static <T> Map<String, T> readModels(List<ResourceCollection> dirs, ModelReader<T> reader) {
    Map<String, T> models = new HashMap<>();
    for (ResourceCollection dir : dirs) {
      for (Resource file : dir.getResources()) {
        if (reader.accepts(file)) {
          try {
            T model = reader.read(file);
            ConfigInheritanceResolver.resolve(model);
            models.put(FilenameUtils.getBaseName(file.getName()), model);
          }
          /*CHECKSTYLE:OFF*/ catch (Exception ex) { /*CHECKSTYLE:ON*/
            throw new GeneratorException("Unable to read definition: " + file.getCanonicalPath(), ex);
          }
        }
      }
    }
    return ImmutableMap.copyOf(models);
  }

  /**
   * Generate files for environment(s).
   * @param environmentNames Environments to generate. If none specified all environments are generated.
   */
  public void generate(String... environmentNames) {
    Map<String, Environment> selectedEnvironments = new HashMap<>();
    if (environmentNames == null || environmentNames.length == 0) {
      selectedEnvironments.putAll(environments);
    }
    else {
      for (String environmentName : environmentNames) {
        if (!environments.containsKey(environmentName)) {
          throw new GeneratorException("Environment '" + environmentName + "' does not exist.");
        }
        selectedEnvironments.put(environmentName, environments.get(environmentName));
      }
    }

    for (Map.Entry<String, Environment> entry : selectedEnvironments.entrySet()) {
      File environmentDestDir = new File(destDir, entry.getKey());
      // remove existing directory and it's content if it exists alreday
      if (deleteBeforeGenerate && environmentDestDir.exists()) {
        try {
          FileUtils.deleteDirectory(environmentDestDir);
        }
        catch (IOException ex) {
          throw new GeneratorException("Unable to delete existing target directory: " + FileUtil.getCanonicalPath(environmentDestDir));
        }
      }
      if (!environmentDestDir.exists()) {
        environmentDestDir.mkdir();
      }
      EnvironmentGenerator environmentGenerator = new EnvironmentGenerator(roles, entry.getKey(), entry.getValue(),
          environmentDestDir, pluginManager, handlebarsManager, urlFileManager, version, dependencyVersions, modelExport, log);
      environmentGenerator.generate();
    }
  }

}
