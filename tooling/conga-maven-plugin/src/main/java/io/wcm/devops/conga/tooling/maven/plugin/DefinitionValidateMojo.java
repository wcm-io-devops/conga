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

import io.wcm.devops.conga.generator.handlebars.HandlebarsManager;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.model.reader.EnvironmentReader;
import io.wcm.devops.conga.model.reader.RoleReader;
import io.wcm.devops.conga.resource.Resource;
import io.wcm.devops.conga.resource.ResourceCollection;
import io.wcm.devops.conga.resource.ResourceLoader;
import io.wcm.devops.conga.tooling.maven.plugin.util.PathUtil;
import io.wcm.devops.conga.tooling.maven.plugin.validation.DefinitionValidator;
import io.wcm.devops.conga.tooling.maven.plugin.validation.ModelValidator;
import io.wcm.devops.conga.tooling.maven.plugin.validation.RoleTemplateFileValidator;
import io.wcm.devops.conga.tooling.maven.plugin.validation.TemplateValidator;

import java.util.SortedSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import com.google.common.collect.ImmutableList;

/**
 * Validates definitions by trying to parse them with model reader or compile them via handlebars.
 */
@Mojo(name = "definition-validate", defaultPhase = LifecyclePhase.VALIDATE, requiresProject = true, threadSafe = true)
public class DefinitionValidateMojo extends AbstractCongaMojo {

  private ResourceLoader resourceLoader;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    resourceLoader = new ResourceLoader();
    validateDefinitions();
  }

  private void validateDefinitions() throws MojoFailureException {
    ResourceCollection roleDir = getRoleDir();
    ResourceCollection templateDir = getTemplateDir();
    ResourceCollection environmentDir = getEnvironmentDir();

    // validate role definition syntax
    validateFiles(roleDir, roleDir, new ModelValidator("Role", new RoleReader()));

    // validate that all template can be compiled
    HandlebarsManager handlebarsManager = new HandlebarsManager(ImmutableList.of(templateDir), new PluginManager());
    validateFiles(templateDir, templateDir, new TemplateValidator(templateDir, handlebarsManager));

    // validate that roles reference existing templates
    validateFiles(roleDir, roleDir, new RoleTemplateFileValidator(handlebarsManager));

    // validate environment definition syntax
    validateFiles(environmentDir, environmentDir, new ModelValidator("Environment", new EnvironmentReader()));
  }

  private void validateFiles(ResourceCollection sourceDir, ResourceCollection rootSourceDir, DefinitionValidator validator)
      throws MojoFailureException {
    if (!sourceDir.exists()) {
      return;
    }
    SortedSet<Resource> files = sourceDir.getResources();
    SortedSet<ResourceCollection> dirs = sourceDir.getResourceCollections();
    if (files.isEmpty() && dirs.isEmpty()) {
      return;
    }

    for (Resource file : files) {
      validator.validate(file, getPathForLog(rootSourceDir, file));
    }
    for (ResourceCollection dir : dirs) {
      validateFiles(dir, rootSourceDir, validator);
    }
  }

  private static String getPathForLog(ResourceCollection rootSourceDir, Resource file) {
    String path = PathUtil.unifySlashes(file.getCanonicalPath());
    String rootPath = PathUtil.unifySlashes(rootSourceDir.getCanonicalPath()) + "/";
    return StringUtils.substringAfter(path, rootPath);
  }


  @Override
  protected ResourceLoader getResourceLoader() {
    return resourceLoader;
  }

}
