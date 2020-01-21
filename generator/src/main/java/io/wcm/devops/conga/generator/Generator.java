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

import com.google.common.collect.ImmutableList;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.wcm.devops.conga.generator.util.FileUtil;
import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.reader.EnvironmentReader;
import io.wcm.devops.conga.resource.ResourceCollection;
import io.wcm.devops.conga.resource.ResourceLoader;

/**
 * Main entry point for CONGA generator.
 */
public final class Generator {

  private final GeneratorOptions options;
  private final File destDir;
  private final Map<String, Environment> environments;

  /**
   * @param options Generator options
   */
  public Generator(GeneratorOptions options) {
    this.options = options;
    this.destDir = FileUtil.ensureDirExistsAutocreate(options.getDestDir());

    ClassLoader resourceClassLoader = ResourceLoaderUtil.buildClassLoader(options.getContainerClasspathUrls());
    ResourceLoader resourceLoader = new ResourceLoader(resourceClassLoader);
    List<ResourceCollection> environmentDirs = ImmutableList.of(
        resourceLoader.getResourceCollection(ResourceLoader.FILE_PREFIX + options.getEnvironmentDir()),
        resourceLoader.getResourceCollection(ResourceLoader.CLASSPATH_PREFIX + GeneratorOptions.CLASSPATH_ENVIRONMENTS_DIR));
    this.environments = ResourceLoaderUtil.readModels(environmentDirs, new EnvironmentReader());
  }

  /**
   * Generate files for environment(s).
   * @param environmentNames Environments to generate. If none specified all environments are generated.
   */
  public void generate(String[] environmentNames) {
    generate(environmentNames, new String[] {});
  }

  /**
   * Generate files for environment(s).
   * @param environmentNames Environments to generate. If none specified all environments are generated.
   * @param nodeNames Node names to generate. If none specified all nodes are generated.
   */
  @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
  public void generate(String[] environmentNames, String[] nodeNames) {
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
      if (options.isDeleteBeforeGenerate() && environmentDestDir.exists()) {
        try {
          FileUtils.deleteDirectory(environmentDestDir);
        }
        catch (IOException ex) {
          throw new GeneratorException("Unable to delete existing target directory: " + FileUtil.getCanonicalPath(environmentDestDir), ex);
        }
      }
      if (!environmentDestDir.exists()) {
        environmentDestDir.mkdir();
      }

      EnvironmentGenerator environmentGenerator = new EnvironmentGenerator(entry.getKey(), entry.getValue(), environmentDestDir, options);
      environmentGenerator.generate(nodeNames);
    }
  }

}
