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

import io.wcm.devops.conga.resource.ResourceCollection;
import io.wcm.devops.conga.resource.ResourceLoader;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Common features for all Mojos.
 */
abstract class AbstractCongaMojo extends AbstractMojo {

  /**
   * Source path with templates.
   */
  @Parameter(defaultValue = "${basedir}/src/main/templates")
  private String templateDir;

  /**
   * Source path with role definitions.
   */
  @Parameter(defaultValue = "${basedir}/src/main/roles")
  private String roleDir;

  /**
   * Target path for the generated configuration files.
   */
  @Parameter(defaultValue = "${project.build.directory}/configuration")
  private String target;

  /**
   * Source path with environment definitions.
   */
  @Parameter(defaultValue = "${basedir}/src/main/environments")
  private String environmentDir;

  protected ResourceCollection getTemplateDir() {
    return getResourceLoader().getResourceCollection(ResourceLoader.FILE_PREFIX + templateDir);
  }

  protected ResourceCollection getRoleDir() {
    return getResourceLoader().getResourceCollection(ResourceLoader.FILE_PREFIX + roleDir);
  }

  protected ResourceCollection getEnvironmentDir() {
    return getResourceLoader().getResourceCollection(ResourceLoader.FILE_PREFIX + environmentDir);
  }

  protected File getTargetDir() {
    return new File(target);
  }

  protected abstract ResourceLoader getResourceLoader();

}
