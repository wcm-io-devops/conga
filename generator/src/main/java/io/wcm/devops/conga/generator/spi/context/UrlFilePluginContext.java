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
package io.wcm.devops.conga.generator.spi.context;

import java.io.File;

import io.wcm.devops.conga.model.environment.Environment;

/**
 * Context for {@link io.wcm.devops.conga.generator.spi.UrlFilePlugin}.
 */
public final class UrlFilePluginContext extends AbstractPluginContext<UrlFilePluginContext> {

  private File baseDir = new File(".");
  private File nodeBaseDir;
  private ClassLoader resourceClassLoader = getClass().getClassLoader();
  private Environment environment;

  /**
   * @return Base directory for resolving relative files in filesystem
   */
  public File getBaseDir() {
    return baseDir;
  }

  /**
   * @param value Base directory for resolving relative files in filesystem
   * @return this
   */
  public UrlFilePluginContext baseDir(File value) {
    baseDir = value;
    return this;
  }

  /**
   * @return Base directory for resolving relative files of the generated files for the current node in filesystem
   */
  public File getNodeBaseDir() {
    return nodeBaseDir;
  }

  /**
   * @param value Base directory for resolving relative files of the generated files for the current node in filesystem
   * @return this
   */
  public UrlFilePluginContext baseNodeDir(File value) {
    nodeBaseDir = value;
    return this;
  }

  /**
   * @param value Class loader for resources
   * @return this
   */
  public UrlFilePluginContext resourceClassLoader(ClassLoader value) {
    resourceClassLoader = value;
    return this;
  }

  /**
   * @return Class loader for resources
   */
  public ClassLoader getResourceClassLoader() {
    return resourceClassLoader;
  }

  /**
   * @return Environment
   */
  public Environment getEnvironment() {
    return this.environment;
  }

  /**
   * @param value Environment
   * @return this
   */
  public UrlFilePluginContext environment(Environment value) {
    this.environment = value;
    return this;
  }

}
