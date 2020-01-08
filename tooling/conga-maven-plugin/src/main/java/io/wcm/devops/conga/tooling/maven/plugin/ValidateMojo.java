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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.google.common.collect.ImmutableList;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.GeneratorOptions;
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
import io.wcm.devops.conga.resource.ResourceInfo;
import io.wcm.devops.conga.resource.ResourceLoader;
import io.wcm.devops.conga.tooling.maven.plugin.util.ClassLoaderUtil;
import io.wcm.devops.conga.tooling.maven.plugin.util.MavenContext;
import io.wcm.devops.conga.tooling.maven.plugin.util.PathUtil;
import io.wcm.devops.conga.tooling.maven.plugin.util.VersionInfoUtil;
import io.wcm.devops.conga.tooling.maven.plugin.validation.DefinitionValidator;
import io.wcm.devops.conga.tooling.maven.plugin.validation.ModelValidator;
import io.wcm.devops.conga.tooling.maven.plugin.validation.NoValueProviderInRoleValidator;
import io.wcm.devops.conga.tooling.maven.plugin.validation.RoleTemplateFileValidator;
import io.wcm.devops.conga.tooling.maven.plugin.validation.TemplateValidator;

/**
 * Validates definitions by trying to parse them with model reader or compile them via handlebars.
 * Validates that the CONGA maven plugin version and CONGA plugin versions match or are newer than those versions
 * used when generating the dependency artifacts.
 */
@Mojo(name = "validate", defaultPhase = LifecyclePhase.VALIDATE, requiresProject = true, threadSafe = true,
    requiresDependencyResolution = ResolutionScope.COMPILE)
public class ValidateMojo extends AbstractCongaMojo {

  /**
   * Selected environments to validate.
   */
  @Parameter(property = "conga.environments")
  private String[] environments;

  @Parameter(property = "project", required = true, readonly = true)
  private MavenProject project;

  @Component
  private RepositorySystem repoSystem;
  @Parameter(property = "repositorySystemSession", readonly = true)
  private RepositorySystemSession repoSession;
  @Parameter(property = "project.remoteProjectRepositories", readonly = true)
  private List<RemoteRepository> remoteRepos;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    List<URL> mavenProjectClasspathUrls = ClassLoaderUtil.getMavenProjectClasspathUrls(project);
    ClassLoader mavenProjectClassLoader = ClassLoaderUtil.buildClassLoader(mavenProjectClasspathUrls);
    ResourceLoader mavenProjectResourceLoader = new ResourceLoader(mavenProjectClassLoader);

    ResourceCollection roleDir = mavenProjectResourceLoader.getResourceCollection(ResourceLoader.FILE_PREFIX + getRoleDir());
    ResourceCollection templateDir = mavenProjectResourceLoader.getResourceCollection(ResourceLoader.FILE_PREFIX + getTemplateDir());
    ResourceCollection environmentDir = mavenProjectResourceLoader.getResourceCollection(ResourceLoader.FILE_PREFIX + getEnvironmentDir());

    // validate role definition syntax
    validateFiles(roleDir, roleDir, new ModelValidator<Role>("Role", new RoleReader()));

    PluginManager pluginManager = new PluginManagerImpl();

    MavenContext mavenContext = new MavenContext()
        .project(project)
        .repoSystem(repoSystem)
        .repoSession(repoSession)
        .remoteRepos(remoteRepos)
        .artifactTypeMappings(getArtifactTypeMappings());

    UrlFilePluginContext urlFilePluginContext = new UrlFilePluginContext()
        .baseDir(project.getBasedir())
        .resourceClassLoader(mavenProjectClassLoader)
        .pluginContextOptions(new PluginContextOptions()
          .containerContext(mavenContext));
    UrlFileManager urlFileManager = new UrlFileManager(pluginManager, urlFilePluginContext);

    PluginContextOptions pluginContextOptions = new PluginContextOptions()
        .pluginManager(pluginManager)
        .urlFileManager(urlFileManager)
        .valueProviderConfig(getValueProviderConfig())
        .genericPluginConfig(getPluginConfig())
        .containerContext(mavenContext)
        .logger(new MavenSlf4jLogFacade(getLog()));

    // validate that all templates can be compiled
    HandlebarsManager handlebarsManager = new HandlebarsManager(ImmutableList.of(templateDir), pluginContextOptions);
    validateFiles(templateDir, templateDir, new TemplateValidator(templateDir, handlebarsManager));

    // validate that roles reference existing templates
    validateFiles(roleDir, roleDir, new RoleTemplateFileValidator(handlebarsManager));

    // validate that no value providers are used in role files - they should be only used in environment
    validateFiles(roleDir, roleDir, new NoValueProviderInRoleValidator());

    // validate environment definition syntax
    List<Environment> environmentList = validateFiles(environmentDir, environmentDir, new ModelValidator<Environment>("Environment", new EnvironmentReader()),
        // filter environments
        resourceInfo -> {
          if (this.environments == null || this.environments.length == 0) {
            return true;
          }
          for (String environment : this.environments) {
            if (StringUtils.equals(environment, FilenameUtils.getBaseName(resourceInfo.getName()))) {
              return true;
            }
          }
          return false;
        });

    // validate version information - for each environment separately
    for (Environment environment : environmentList) {
      UrlFilePluginContext environmentUrlFilePluginContext = new UrlFilePluginContext()
          .baseDir(project.getBasedir())
          .resourceClassLoader(mavenProjectClassLoader)
          .environment(environment)
          .pluginContextOptions(new PluginContextOptions()
              .containerContext(mavenContext));
      UrlFileManager environmentUrlFileManager = new UrlFileManager(pluginManager, environmentUrlFilePluginContext);

      PluginContextOptions environmentPluginContextOptions = new PluginContextOptions()
          .pluginContextOptions(pluginContextOptions)
          .urlFileManager(environmentUrlFileManager);
      validateVersionInfo(environment, mavenProjectClasspathUrls, environmentPluginContextOptions);
    }
  }

  // ===== FILE VALIDATION =====

  private <T> List<T> validateFiles(ResourceCollection sourceDir, ResourceCollection rootSourceDir, DefinitionValidator<T> validator)
      throws MojoFailureException {
    return validateFiles(sourceDir, rootSourceDir, validator, resourceInfo -> true);
  }

  private <T> List<T> validateFiles(ResourceCollection sourceDir, ResourceCollection rootSourceDir, DefinitionValidator<T> validator,
      Function<ResourceInfo, Boolean> resourceFilter) throws MojoFailureException {
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
      if (resourceFilter.apply(file)) {
        result.add(validator.validate(file, getPathForLog(rootSourceDir, file)));
      }
    }
    for (ResourceCollection dir : dirs) {
      if (resourceFilter.apply(dir)) {
        result.addAll(validateFiles(dir, rootSourceDir, validator, resourceFilter));
      }
    }
    return result;
  }

  private static String getPathForLog(ResourceCollection rootSourceDir, Resource file) {
    String path = PathUtil.unifySlashes(file.getCanonicalPath());
    String rootPath = PathUtil.unifySlashes(rootSourceDir.getCanonicalPath()) + "/";
    return StringUtils.substringAfter(path, rootPath);
  }

  // ===== PLUGIN VERSION INFO VALIDATION =====

  /**
   * Validates that the CONGA maven plugin version and CONGA plugin versions match or are newer than those versions used
   * when generating the dependency artifacts.
   * @param environment Environment
   * @param mavenProjectClasspathUrls Classpath URLs of maven project
   * @param pluginContextOptions Plugin context options
   */
  private void validateVersionInfo(Environment environment, List<URL> mavenProjectClasspathUrls, PluginContextOptions pluginContextOptions)
      throws MojoExecutionException {

    // build combined classpath for dependencies defined in environment and maven project
    List<URL> classpathUrls = new ArrayList<>();
    classpathUrls.addAll(getEnvironmentClasspathUrls(environment.getDependencies(), pluginContextOptions));
    classpathUrls.addAll(mavenProjectClasspathUrls);
    ClassLoader environmentDependenciesClassLoader = ClassLoaderUtil.buildClassLoader(classpathUrls);

    // get version info from this project
    Properties currentVersionInfo = VersionInfoUtil.getVersionInfoProperties(project);

    // validate current version info against dependency version infos
    for (Properties dependencyVersionInfo : getDependencyVersionInfos(environmentDependenciesClassLoader)) {
      validateVersionInfo(currentVersionInfo, dependencyVersionInfo);
    }

  }

  private List<URL> getEnvironmentClasspathUrls(List<String> dependencyUrls, PluginContextOptions pluginContextOptions) {
    return dependencyUrls.stream()
        .map(dependencyUrl -> {
          String resolvedDependencyUrl = ClassLoaderUtil.resolveDependencyUrl(dependencyUrl, pluginContextOptions);
          try {
            return pluginContextOptions.getUrlFileManager().getFileUrlsWithDependencies(resolvedDependencyUrl);
          }
          catch (IOException ex) {
            throw new GeneratorException("Unable to resolve: " + resolvedDependencyUrl, ex);
          }
        })
        .flatMap(list -> list.stream())
        .collect(Collectors.toList());
  }

  private void validateVersionInfo(Properties currentVersionInfo, Properties dependencyVersionInfo) throws MojoExecutionException {
    for (Object keyObject : currentVersionInfo.keySet()) {
      String key = keyObject.toString();
      String currentVersionString = currentVersionInfo.getProperty(key);
      String dependencyVersionString = dependencyVersionInfo.getProperty(key);
      if (StringUtils.isEmpty(currentVersionString) || StringUtils.isEmpty(dependencyVersionString)) {
        continue;
      }
      DefaultArtifactVersion currentVersion = new DefaultArtifactVersion(currentVersionString);
      DefaultArtifactVersion dependencyVersion = new DefaultArtifactVersion(dependencyVersionString);
      if (currentVersion.compareTo(dependencyVersion) < 0) {
        throw new MojoExecutionException("Newer CONGA maven plugin or plugin version required: " + key + ":" + dependencyVersion.toString());
      }
    }
  }

  private List<Properties> getDependencyVersionInfos(ClassLoader classLoader) throws MojoExecutionException {
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
    try {
      org.springframework.core.io.Resource[] resources = resolver.getResources(
          "classpath*:" + GeneratorOptions.CLASSPATH_PREFIX + BuildConstants.FILE_VERSION_INFO);
      return Arrays.stream(resources)
          .map(resource -> toProperties(resource))
          .collect(Collectors.toList());
    }
    catch (IOException ex) {
      throw new MojoExecutionException("Unable to get classpath resources: " + ex.getMessage(), ex);
    }
  }

  private Properties toProperties(org.springframework.core.io.Resource resource) {
    try (InputStream is = resource.getInputStream()) {
      Properties props = new Properties();
      props.load(is);
      return props;
    }
    catch (IOException ex) {
      throw new RuntimeException("Unable to read properties file: " + resource.toString(), ex);
    }
  }

}
