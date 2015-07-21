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
package io.wcm.devops.conga.tooling.maven.plugin.util;

import io.wcm.devops.conga.tooling.maven.plugin.BuildConstants;

import java.util.Properties;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;

/**
 * Utility method for generating and validation version info.
 */
public final class VersionInfoUtil {

  private VersionInfoUtil() {
    // static methods only
  }

  /**
   * Detects versions from maven-conga-plugin and additional CONGA plugins used.
   * @return Properties with versions
   */
  public static Properties getVersionInfoProperties(MavenProject project) {
    Properties props = new Properties();

    // add version info about the conga-maven-plugin itself, and direct plugin dependencies (assumed conga plugins)
    Plugin congaPlugin = project.getPlugin(BuildConstants.CONGA_MAVEN_PLUGIN_KEY);
    if (congaPlugin != null) {
      props.put(congaPlugin.getKey(), congaPlugin.getVersion());

      congaPlugin.getDependencies().stream()
      .forEach(dependency -> props.put(Plugin.constructKey(dependency.getGroupId(), dependency.getArtifactId()), dependency.getVersion()));
    }

    return props;
  }

}
