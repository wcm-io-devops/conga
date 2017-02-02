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
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import io.wcm.devops.conga.tooling.maven.plugin.util.ClassLoaderUtil;
import io.wcm.devops.conga.tooling.maven.plugin.util.VersionInfoUtil;

/**
 * Validates that the CONGA maven plugin version and CONGA plugin versions match or are newer than those versions
 * used when generating the dependency artifacts.
 */
@Mojo(name = "validate-version-info", defaultPhase = LifecyclePhase.VALIDATE, requiresProject = true, threadSafe = true,
requiresDependencyResolution = ResolutionScope.COMPILE)
public class ValidateVersionInfoMojo extends AbstractMojo {

  @Parameter(property = "project", required = true, readonly = true)
  private MavenProject project;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {

    // get version info from this project
    Properties currentVersionInfo = VersionInfoUtil.getVersionInfoProperties(project);

    // validate current version info against dependency version infos
    for (Properties dependencyVersionInfo : getDependencyVersionInfos()) {
      validateVersionInfo(currentVersionInfo, dependencyVersionInfo);
    }
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

  private List<Properties> getDependencyVersionInfos() throws MojoExecutionException {
    ClassLoader classLoader = ClassLoaderUtil.buildDependencyClassLoader(project);
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
    try {
      Resource[] resources = resolver.getResources(
          "classpath*:" + BuildConstants.CLASSPATH_PREFIX + BuildConstants.FILE_VERSION_INFO);
      return Arrays.stream(resources)
          .map(resource -> toProperties(resource))
          .collect(Collectors.toList());
    }
    catch (IOException ex) {
      throw new MojoExecutionException("Unable to get classpath resources: " + ex.getMessage(), ex);
    }
  }

  private Properties toProperties(Resource resource) {
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
