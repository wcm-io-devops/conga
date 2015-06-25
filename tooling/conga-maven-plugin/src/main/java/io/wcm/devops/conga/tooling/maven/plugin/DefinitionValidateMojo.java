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
import io.wcm.devops.conga.model.reader.ModelReader;
import io.wcm.devops.conga.model.reader.RoleReader;
import io.wcm.devops.conga.resource.Resource;
import io.wcm.devops.conga.resource.ResourceCollection;
import io.wcm.devops.conga.resource.ResourceLoader;

import java.util.List;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import com.github.jknack.handlebars.Handlebars;
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

    validateFiles(roleDir, roleDir, new ModelValidator("Role", new RoleReader()));
    validateFiles(templateDir, templateDir, new TemplateValidator(templateDir));
    validateFiles(environmentDir, environmentDir, new ModelValidator("Environment", new EnvironmentReader()));
  }

  private void validateFiles(ResourceCollection sourceDir, ResourceCollection rootSourceDir, Validator validator)
      throws MojoFailureException {
    if (!sourceDir.exists()) {
      return;
    }
    List<Resource> files = sourceDir.getResources();
    List<ResourceCollection> dirs = sourceDir.getResourceCollections();
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
    String path = unifySlashes(file.getCanonicalPath());
    String rootPath = unifySlashes(rootSourceDir.getCanonicalPath()) + "/";
    return StringUtils.substringAfter(path, rootPath);
  }

  private static String unifySlashes(String path) {
    return StringUtils.replace(path, "\\", "/");
  }

  @Override
  protected ResourceLoader getResourceLoader() {
    return resourceLoader;
  }

  /**
   * Resource validator
   */
  private interface Validator {
    void validate(Resource resource, String pathForLog) throws MojoFailureException;
  }

  /**
   * Validates YAML model file against it's reader.
   */
  private static class ModelValidator implements Validator {

    private final String modelName;
    private final ModelReader<?> modelReader;

    public ModelValidator(String modelName, ModelReader<?> modelReader) {
      this.modelName = modelName;
      this.modelReader = modelReader;
    }

    @Override
    public void validate(Resource resource, String pathForLog) throws MojoFailureException {
      try {
        modelReader.read(resource);
      }
      catch (Throwable ex) {
        throw new MojoFailureException(modelName + " definition " + pathForLog + " is invalid:\n" + ex.getMessage());
      }
    }

  }

  /**
   * Validates Handlebars templates by compiling it.
   */
  private static class TemplateValidator implements Validator {

    private static final String FILE_EXTENSION = "hbs";

    private final ResourceCollection templateDir;
    private final HandlebarsManager handlebarsManager;

    public TemplateValidator(ResourceCollection templateDir) {
      this.templateDir = templateDir;
      this.handlebarsManager = new HandlebarsManager(ImmutableList.of(templateDir), new PluginManager());
    }

    @Override
    public void validate(Resource resource, String pathForLog) throws MojoFailureException {
      if (StringUtils.equalsIgnoreCase(resource.getFileExtension(), FILE_EXTENSION)) {
        String templatePath = StringUtils.substringAfter(unifySlashes(resource.getCanonicalPath()),
            unifySlashes(templateDir.getCanonicalPath()) + "/");
        Handlebars handlebars = handlebarsManager.get(null, CharEncoding.UTF_8);
        try {
          handlebars.compile(templatePath);
        }
        catch (Throwable ex) {
          throw new MojoFailureException("Template " + pathForLog + " is invalid:\n" + ex.getMessage());
        }
      }
    }

  }

}
