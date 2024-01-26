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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import io.wcm.devops.conga.generator.Generator;
import io.wcm.devops.conga.generator.GeneratorOptions;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;

/**
 * CONGA command line interface.
 */
@SuppressWarnings("java:S1192") // duplicate string literals
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
    CLI_OPTIONS.addOption("nodes", true, "Selected nodes to generate (separated by ',').");
    CLI_OPTIONS.addOption("?", false, "Print usage help.");
  }

  private CongaCli() {
    // static methods only
  }

  /**
   * CLI entry point
   * @param args Command line arguments
   * @throws ParseException Parse exceptoin
   */
  //CHECKSTYLE:OFF
  public static void main(String[] args) throws ParseException {
    //CHECKSTYLE:ON
    CommandLine commandLine = new DefaultParser().parse(CLI_OPTIONS, args);

    if (commandLine.hasOption("?")) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.setWidth(150);
      formatter.printHelp("java -jar io.wcm.devops.conga.tooling.cli-<version>.jar <arguments>", CLI_OPTIONS);
      return;
    }

    File baseDir = new File(".");
    File templateDir = new File(commandLine.getOptionValue("templateDir", "templates"));
    File roleDir = new File(commandLine.getOptionValue("roleDir", "roles"));
    File environmentDir = new File(commandLine.getOptionValue("environmentDir", "environments"));
    File targetDir = new File(commandLine.getOptionValue("target", "target"));
    String[] environments = StringUtils.split(commandLine.getOptionValue("environments", null), ",");
    String[] nodes = StringUtils.split(commandLine.getOptionValue("nodes", null), ",");

    GeneratorOptions options = new GeneratorOptions()
        .baseDir(baseDir)
        .roleDir(roleDir)
        .templateDir(templateDir)
        .environmentDir(environmentDir)
        .destDir(targetDir)
        .deleteBeforeGenerate(true)
        .pluginManager(new PluginManagerImpl());

    Generator generator = new Generator(options);
    generator.generate(environments, nodes);
  }

}
