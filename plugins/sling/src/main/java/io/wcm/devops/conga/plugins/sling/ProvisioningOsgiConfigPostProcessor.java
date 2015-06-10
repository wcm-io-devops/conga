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
package io.wcm.devops.conga.plugins.sling;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.PostProcessorPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.cm.file.ConfigurationHandler;
import org.apache.sling.provisioning.model.Configuration;
import org.apache.sling.provisioning.model.Feature;
import org.apache.sling.provisioning.model.Model;
import org.apache.sling.provisioning.model.RunMode;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;

/**
 * Transforms a Sling Provisioning file into OSGi configurations (ignoring all other provisioning contents).
 */
public class ProvisioningOsgiConfigPostProcessor implements PostProcessorPlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "sling-provisioning-osgiconfig";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean accepts(File file, String charset) {
    return ProvisioningUtil.isProvisioningFile(file, charset);
  }

  @Override
  public void postProcess(File file, String charset, Logger logger) {
    try {
      // generate OSGi configurations
      Model model = ProvisioningUtil.getModel(file, charset);
      generateOsgiConfigurations(model, file.getParentFile(), logger);

      // delete provisioning file after transformation
      file.delete();
    }
    catch (IOException ex) {
      throw new GeneratorException("Unable to post-process sling provisioning OSGi configurations.", ex);
    }
  }

  /**
   * Generate OSGi configuration for all feature and run modes.
   * @param model Provisioning Model
   * @param dir Target directory
   * @param logger Logger
   * @throws IOException
   */
  private void generateOsgiConfigurations(Model model, File dir, Logger logger) throws IOException {
    for (Feature feature : model.getFeatures()) {
      for (RunMode runMode : feature.getRunModes()) {
        for (Configuration configuration : runMode.getConfigurations()) {
          String path = getPathForConfiguration(configuration, runMode);
          logger.info("  Generate " + path);

          File confFile = new File(dir, path);
          confFile.getParentFile().mkdirs();
          try (FileOutputStream os = new FileOutputStream(confFile)) {
            ConfigurationHandler.write(os, configuration.getProperties());
          }
        }
      }
    }
  }

  /**
   * Get the relative path for a configuration
   */
  private String getPathForConfiguration(Configuration configuration, RunMode runMode) {
    SortedSet<String> runModesList = new TreeSet<>();
    if (runMode.getNames() != null) {
      runModesList.addAll(ImmutableList.copyOf(runMode.getNames()));
    }

    // run modes directory
    StringBuilder path = new StringBuilder();
    if (!runModesList.isEmpty() && !runMode.isSpecial()) {
      path.append(StringUtils.join(runModesList, ".")).append("/");
    }

    // main name
    if (configuration.getFactoryPid() != null) {
      path.append(configuration.getFactoryPid()).append("-");
    }
    path.append(configuration.getPid()).append(".config");

    return path.toString();
  }

}
