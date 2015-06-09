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

import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.reader.EnvironmentReader;
import io.wcm.devops.conga.model.reader.ModelReader;
import io.wcm.devops.conga.model.reader.RoleReader;
import io.wcm.devops.conga.model.resolver.ConfigResolver;
import io.wcm.devops.conga.model.role.Role;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.google.common.collect.ImmutableMap;

/**
 * Main entry point for CONGA generator.
 */
public class Generator {

  private final Map<String, Role> roles;
  private final Map<String, Environment> environments;
  private final File destDir;

  /**
   * @param roleDir Directory with role definitions. Filename without extension = role name.
   * @param environmentDir Directory with environment definitions. Filename without extension = environment name.
   */
  public Generator(File roleDir, File environmentDir, File destDir) {
    try {
      this.roles = readModels(roleDir, new RoleReader());
      this.environments = readModels(environmentDir, new EnvironmentReader());
      this.destDir = validateDestDir(destDir);
    }
    catch (IOException ex) {
      throw new GeneratorException("Unable to set up generator.", ex);
    }
  }

  private static <T> Map<String, T> readModels(File dir, ModelReader<T> reader) throws IOException {
    if (!dir.exists() || !dir.isDirectory()) {
      throw new IllegalArgumentException("Expected directory: " + dir.getCanonicalPath());
    }
    Map<String, T> models = new HashMap<>();
    for (File file : dir.listFiles()) {
      if (reader.accepts(file)) {
        try {
          T model = reader.read(file);
          ConfigResolver.resolve(model);
          models.put(FilenameUtils.getBaseName(file.getName()), model);
        }
        catch (Throwable ex) {
          throw new GeneratorException("Unable to read definition: " + file.getCanonicalPath(), ex);
        }
      }
    }
    return ImmutableMap.copyOf(models);
  }

  private static File validateDestDir(File dir) throws IOException {
    if (!dir.exists()) {
      dir.mkdirs();
    }
    if (!dir.isDirectory()) {
      throw new IllegalArgumentException("Expected directory: " + dir.getCanonicalPath());
    }
    return dir;
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
      File environmentDir = new File(destDir, entry.getKey());
      // remove existing directory and it's content if it exists alreday
      if (environmentDir.exists()) {
        environmentDir.delete();
      }
      environmentDir.mkdir();
      EnvironmentGenerator environmentGenerator = new EnvironmentGenerator(roles, entry.getKey(), entry.getValue(), environmentDir);
      environmentGenerator.generate();
    }
  }

}
