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

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;

import io.wcm.devops.conga.tooling.maven.plugin.BuildConstants;

/**
 * Utility method for generating and validation version info.
 */
public final class VersionInfoUtil {

  // match versions like 2.1.2-20180125.094723-16
  private static final Pattern SNAPSHOT_VERSION_PATTERN = Pattern.compile("(\\d+(\\.\\d+)*)-(\\d{8}\\.\\d{6}\\-\\d+)");

  private VersionInfoUtil() {
    // static methods only
  }

  /**
   * Detects versions from maven-conga-plugin and additional CONGA plugins used.
   * @param project Maven project
   * @return Properties with versions
   */
  public static Properties getVersionInfoProperties(MavenProject project) {
    Properties props = new Properties();

    // add version info about the conga-maven-plugin itself, and direct plugin dependencies (assumed conga plugins)
    Plugin congaPlugin = project.getPlugin(BuildConstants.CONGA_MAVEN_PLUGIN_KEY);
    if (congaPlugin != null) {
      props.put(congaPlugin.getKey(), congaPlugin.getVersion());

      congaPlugin.getDependencies().stream()
          .forEach(dependency -> {
            String artifactKey = Plugin.constructKey(dependency.getGroupId(), dependency.getArtifactId());
            props.put(artifactKey, cleanupSnapshotVersion(dependency.getVersion()));
          });
    }

    return props;
  }

  /**
   * Replaces "static" snapshot version like 2.1.2-20180125.094723-16 with dynamic ones like 2.1.2-SNAPSHOT.
   * @param versionLine Version line
   * @return Reformatted version line
   */
  public static String cleanupSnapshotVersion(String versionLine) {
    Matcher matcher = SNAPSHOT_VERSION_PATTERN.matcher(versionLine);
    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      String version = matcher.group(1) + "-SNAPSHOT";
      matcher.appendReplacement(sb, version);
    }
    matcher.appendTail(sb);
    return sb.toString();
  }

  /**
   * Get version of Maven plugin. If it is not defined as active plugin in the current project,
   * try to fetch version from plugin management.
   * @param pluginKey Plugin key
   * @param project Maven project
   * @return Plugin version or null if not defined
   */
  public static String getPluginVersionFromPluginManagement(String pluginKey, MavenProject project) {

    // try to resolve as active plugin in current project
    Plugin plugin = project.getPlugin(pluginKey);
    if (plugin != null) {
      return plugin.getVersion();
    }

    // try to fetch version from plugin management
    plugin = project.getPluginManagement().getPluginsAsMap().get(pluginKey);
    if (plugin != null) {
      return plugin.getVersion();
    }

    return null;
  }

}
