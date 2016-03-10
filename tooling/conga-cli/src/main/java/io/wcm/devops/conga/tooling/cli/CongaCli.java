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
package io.wcm.devops.conga.tooling.cli;

import java.io.File;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;

import io.wcm.devops.conga.generator.Generator;
import io.wcm.devops.conga.resource.ResourceCollection;
import io.wcm.devops.conga.resource.ResourceLoader;

/**
 * CONGA command line interface.
 */
public final class CongaCli {

  /**
   * Command line options
   */
  public static final Options CLI_OPTIONS = new Options();
  static {
    CLI_OPTIONS.addOption("templateDir", true, "Source path with templates.");
    CLI_OPTIONS.addOption("roleDir", true, "Source path with role definitions.");
    CLI_OPTIONS.addOption("environmentDir", true, "Source path with environment definitions.");
    CLI_OPTIONS.addOption("target", true, "Target path for the generated configuration files.");
    CLI_OPTIONS.addOption("environments", true, "Selected environments to generate (separated by ',').");
    CLI_OPTIONS.addOption("?", false, "Print usage help.");
  }

  private CongaCli() {
    // static methods only
  }

  /**
   * CLI entry point
   * @param args Command line arguments
   * @throws Exception
   */
  //CHECKSTYLE:OFF
  public static void main(String[] args) throws Exception {
    //CHECKSTYLE:ON
    CommandLine commandLine = new DefaultParser().parse(CLI_OPTIONS, args);

    if (commandLine.hasOption("?")) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.setWidth(150);
      formatter.printHelp("java -cp io.wcm.devops.conga.tooling.cli-<version>.jar "
          + "io.wcm.devops.conga.tooling.cli.CongaCli <arguments>", CLI_OPTIONS);
      return;
    }

    String templateDir = commandLine.getOptionValue("templateDir", "templates");
    String roleDir = commandLine.getOptionValue("roleDir", "roles");
    String environmentDir = commandLine.getOptionValue("environmentDir", "environments");
    String targetDir = commandLine.getOptionValue("target", "target");
    String[] environments = StringUtils.split(commandLine.getOptionValue("environments", null), ",");

    ResourceLoader resourceLoader = new ResourceLoader();
    List<ResourceCollection> roleDirs = ImmutableList.of(resourceLoader.getResourceCollection(ResourceLoader.FILE_PREFIX + roleDir));
    List<ResourceCollection> templateDirs = ImmutableList.of(resourceLoader.getResourceCollection(ResourceLoader.FILE_PREFIX + templateDir));
    List<ResourceCollection> environmentDirs = ImmutableList.of(resourceLoader.getResourceCollection(ResourceLoader.FILE_PREFIX + environmentDir));
    File targetDirecotry = new File(targetDir);

    Generator generator = new Generator(roleDirs, templateDirs, environmentDirs, targetDirecotry);
    generator.setDeleteBeforeGenerate(true);
    generator.generate(environments);
  }

}
