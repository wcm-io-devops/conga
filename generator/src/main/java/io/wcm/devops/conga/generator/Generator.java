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

import io.wcm.devops.conga.generator.handlebars.HandlebarsManager;
import io.wcm.devops.conga.generator.spi.context.UrlFilePluginContext;
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
  private Logger log = LoggerFactory.getLogger(getClass());
  private boolean deleteBeforeGenerate;
  private String version;
  private List<String> dependencyVersions;

  /**
   * @param roleDirs Directories with role definitions. Filename without extension = role name.
   * @param templateDirs Template base directories
   * @param environmentDirs Directories with environment definitions. Filename without extension = environment name.
   * @param destDir Destination directory for generated file
   * @param urlFilePluginContext URL file plugin context
   */
  public Generator(List<ResourceCollection> roleDirs, List<ResourceCollection> templateDirs, List<ResourceCollection> environmentDirs,
      File destDir, UrlFilePluginContext urlFilePluginContext) {
    this.pluginManager = new PluginManagerImpl();
    this.roles = readModels(roleDirs, new RoleReader());
    this.environments = readModels(environmentDirs, new EnvironmentReader());
    this.destDir = FileUtil.ensureDirExistsAutocreate(destDir);
    this.handlebarsManager = new HandlebarsManager(templateDirs, this.pluginManager);
    this.urlFileManager = new UrlFileManager(this.pluginManager, urlFilePluginContext);
  }

  /**
   * @param logger Logger to use for generation process logging.
   */
  public void setLogger(Logger logger) {
    log = logger;
  }

  /**
   * @param deleteBeforeGenerate Set to true when the generate should delete the environment folders before generating
   *          new (default: false)
   */
  public void setDeleteBeforeGenerate(boolean deleteBeforeGenerate) {
    this.deleteBeforeGenerate = deleteBeforeGenerate;
  }

  /**
   * @param version The main version of the environment definition.
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * @param dependencyVersions List of versions to include as dependency information in generated file headers,
   *          e.g. the versions of the references role/template definition artifacts.
   */
  public void setDependencyVersions(List<String> dependencyVersions) {
    this.dependencyVersions = dependencyVersions;
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
          catch (Throwable ex) {
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
          environmentDestDir, pluginManager, handlebarsManager, urlFileManager, version, dependencyVersions, log);
      environmentGenerator.generate();
    }
  }

}
