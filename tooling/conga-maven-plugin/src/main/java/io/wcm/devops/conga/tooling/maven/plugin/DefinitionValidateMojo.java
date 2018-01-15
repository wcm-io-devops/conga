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

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;

import com.google.common.collect.ImmutableList;

import io.wcm.devops.conga.generator.UrlFileManager;
import io.wcm.devops.conga.generator.handlebars.HandlebarsManager;
import io.wcm.devops.conga.generator.spi.context.PluginContextOptions;
import io.wcm.devops.conga.generator.spi.context.UrlFilePluginContext;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;
import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.reader.EnvironmentReader;
import io.wcm.devops.conga.model.reader.RoleReader;
import io.wcm.devops.conga.model.role.Role;
import io.wcm.devops.conga.resource.Resource;
import io.wcm.devops.conga.resource.ResourceCollection;
import io.wcm.devops.conga.resource.ResourceLoader;
import io.wcm.devops.conga.tooling.maven.plugin.urlfile.MavenUrlFilePluginContext;
import io.wcm.devops.conga.tooling.maven.plugin.util.ClassLoaderUtil;
import io.wcm.devops.conga.tooling.maven.plugin.util.PathUtil;
import io.wcm.devops.conga.tooling.maven.plugin.validation.DefinitionValidator;
import io.wcm.devops.conga.tooling.maven.plugin.validation.ModelValidator;
import io.wcm.devops.conga.tooling.maven.plugin.validation.RoleTemplateFileValidator;
import io.wcm.devops.conga.tooling.maven.plugin.validation.TemplateValidator;

/**
 * Validates definitions by trying to parse them with model reader or compile them via handlebars.
 */
@Mojo(name = "definition-validate", defaultPhase = LifecyclePhase.VALIDATE, requiresProject = true, threadSafe = true)
public class DefinitionValidateMojo extends AbstractCongaMojo {

  @Parameter(property = "project", required = true, readonly = true)
  private MavenProject project;

  @Component
  private ArtifactResolver resolver;
  @Component
  private ArtifactHandlerManager artifactHandlerManager;
  @Parameter(property = "session", readonly = true, required = true)
  private MavenSession mavenSession;

  @Component
  private RepositorySystem repository;
  @Parameter(property = "localRepository", required = true, readonly = true)
  private ArtifactRepository localRepository;
  @Parameter(property = "project.remoteArtifactRepositories", required = true, readonly = true)
  private java.util.List<ArtifactRepository> remoteRepositories;

  private ResourceLoader resourceLoader;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    resourceLoader = new ResourceLoader();
    ClassLoader resourceClassLoader = ClassLoaderUtil.buildDependencyClassLoader(project);

    ResourceCollection roleDir = getResourceLoader().getResourceCollection(ResourceLoader.FILE_PREFIX + getRoleDir());
    ResourceCollection templateDir = getResourceLoader().getResourceCollection(ResourceLoader.FILE_PREFIX + getTemplateDir());
    ResourceCollection environmentDir = getResourceLoader().getResourceCollection(ResourceLoader.FILE_PREFIX + getEnvironmentDir());

    // validate role definition syntax
    validateFiles(roleDir, roleDir, new ModelValidator<Role>("Role", new RoleReader()));

    UrlFilePluginContext urlFilePluginContext = new UrlFilePluginContext()
        .baseDir(project.getBasedir())
        .resourceClassLoader(resourceClassLoader)
        .containerContext(new MavenUrlFilePluginContext()
            .project(project)
            .repository(repository)
            .localRepository(localRepository)
            .remoteRepositories(remoteRepositories));

    PluginManager pluginManager = new PluginManagerImpl();
    PluginContextOptions pluginContextOptions = new PluginContextOptions()
        .pluginManager(pluginManager)
        .urlFileManager(new UrlFileManager(pluginManager, urlFilePluginContext))
        .genericPluginConfig(getPluginConfig())
        .logger(new MavenSlf4jLogFacade(getLog()));

    // validate that all templates can be compiled
    HandlebarsManager handlebarsManager = new HandlebarsManager(ImmutableList.of(templateDir), pluginContextOptions);
    validateFiles(templateDir, templateDir, new TemplateValidator(templateDir, handlebarsManager));

    // validate that roles reference existing templates
    validateFiles(roleDir, roleDir, new RoleTemplateFileValidator(handlebarsManager));

    // validate environment definition syntax
    validateFiles(environmentDir, environmentDir, new ModelValidator<Environment>("Environment", new EnvironmentReader()));
  }

  private <T> List<T> validateFiles(ResourceCollection sourceDir, ResourceCollection rootSourceDir, DefinitionValidator<T> validator)
      throws MojoFailureException {
    if (!sourceDir.exists()) {
      return ImmutableList.of();
    }
    SortedSet<Resource> files = sourceDir.getResources();
    SortedSet<ResourceCollection> dirs = sourceDir.getResourceCollections();
    if (files.isEmpty() && dirs.isEmpty()) {
      return ImmutableList.of();
    }

    List<T> result = new ArrayList<>();
    for (Resource file : files) {
      result.add(validator.validate(file, getPathForLog(rootSourceDir, file)));
    }
    for (ResourceCollection dir : dirs) {
      result.addAll(validateFiles(dir, rootSourceDir, validator));
    }
    return result;
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
