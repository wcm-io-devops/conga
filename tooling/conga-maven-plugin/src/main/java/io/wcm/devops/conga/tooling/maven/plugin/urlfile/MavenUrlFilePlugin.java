/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2016 wcm.io
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
package io.wcm.devops.conga.tooling.maven.plugin.urlfile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.aether.artifact.Artifact;

import io.wcm.devops.conga.generator.spi.UrlFilePlugin;
import io.wcm.devops.conga.generator.spi.context.UrlFilePluginContext;
import io.wcm.devops.conga.tooling.maven.plugin.util.MavenArtifactHelper;

/**
 * Download files from Maven artifact repository.
 */
public class MavenUrlFilePlugin implements UrlFilePlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "maven";

  /**
   * Url prefix
   */
  public static final String PREFIX = "mvn:";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean accepts(String url, UrlFilePluginContext context) {
    return StringUtils.startsWith(url, PREFIX);
  }

  @Override
  public String getFileName(String url, UrlFilePluginContext context) throws IOException {
    File file = getLocalFile(url, context);
    return file.getName();
  }

  @Override
  public InputStream getFile(String url, UrlFilePluginContext context) throws IOException {
    File file = getLocalFile(url, context);
    return new BufferedInputStream(new FileInputStream(file));
  }

  @Override
  public URL getFileUrl(String url, UrlFilePluginContext context) throws IOException {
    File file = getLocalFile(url, context);
    return file.toURI().toURL();
  }

  @Override
  public List<URL> getFileUrlsWithDependencies(String url, UrlFilePluginContext context) throws IOException {
    MavenArtifactHelper mavenArtifactHelper = new MavenArtifactHelper(context.getEnvironment(), context.getPluginContextOptions());
    List<URL> urls = new ArrayList<>();

    // add artifact itself
    Artifact artifact = mavenArtifactHelper.resolveArtifact(getMavenCoords(url));
    urls.add(artifact.getFile().toURI().toURL());

    // get transitive dependencies of artifact
    for (Artifact dependencyArtifact : mavenArtifactHelper.getTransitiveDependencies(artifact)) {
      urls.add(dependencyArtifact.getFile().toURI().toURL());
    }

    return urls;
  }

  @Override
  public boolean isLocalFile(String url, UrlFilePluginContext context) throws IOException {
    File file = getLocalFile(url, context);
    return file != null;
  }

  @Override
  public File getLocalFile(String url, UrlFilePluginContext context) throws IOException {
    MavenArtifactHelper mavenArtifactHelper = new MavenArtifactHelper(context.getEnvironment(), context.getPluginContextOptions());
    return mavenArtifactHelper.resolveArtifact(getMavenCoords(url)).getFile();
  }

  /**
   * Get Maven coordinates
   * @param url Maven url staring with mvn:
   * @return Maven coordinates
   */
  public static String getMavenCoords(String url) {
    return StringUtils.substringAfter(url, PREFIX);
  }

}
