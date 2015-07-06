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
package io.wcm.devops.conga.tooling.maven.plugin;

/**
 * Packaging types
 */
public final class BuildConstants {

  private BuildConstants() {
    // constants only
  }

  /**
   * Builds a JAR artifact with definition files (roles, templates, environments are possible).
   */
  public static final String PACKAGING_DEFINITION = "config-definition";

  /**
   * Classifier for JAR artifact with definition files (roles, templates, environments are possible).
   * This is only used if the packing type of the maven project is not {@link #PACKAGING_DEFINITION}.
   */
  public static final String CLASSIFIER_DEFINITION = "config-definition";

  /**
   * File extension for the artifact with definition files (roles, templates, environments are possible).
   */
  public static final String FILE_EXTENSION_DEFINITION = "jar";

  /**
   * Builds a ZIP artifact with the generated configuration.
   */
  public static final String PACKAGING_CONFIGURATION = "config";

  /**
   * Classifier for ZIP artifact with the generated configuration.
   * This is only used if the packing type of the maven project is not {@link #PACKAGING_CONFIGURATION}.
   */
  public static final String CLASSIFIER_CONFIGURATION = "config";

  /**
   * File extension for artifact with the generated configuration.
   */
  public static final String FILE_EXTENSION_CONFIGURATION = "zip";

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

}
