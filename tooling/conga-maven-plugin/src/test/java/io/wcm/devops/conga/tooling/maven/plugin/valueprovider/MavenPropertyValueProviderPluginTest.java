/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2018 wcm.io
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
package io.wcm.devops.conga.tooling.maven.plugin.valueprovider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import io.wcm.devops.conga.generator.spi.ValueProviderPlugin;
import io.wcm.devops.conga.generator.spi.context.PluginContextOptions;
import io.wcm.devops.conga.generator.spi.context.ValueProviderContext;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;
import io.wcm.devops.conga.tooling.maven.plugin.util.MavenContext;


public class MavenPropertyValueProviderPluginTest {

  private ValueProviderPlugin underTest;
  private ValueProviderContext context;

  private MavenProject mavenProject;

  @Before
  public void setUp() {
    mavenProject = new MavenProject();

    PluginManager pluginManager = new PluginManagerImpl();
    underTest = pluginManager.get(MavenPropertyValueProviderPlugin.NAME, ValueProviderPlugin.class);
    PluginContextOptions pluginContextOptions = new PluginContextOptions()
        .pluginManager(pluginManager)
        .pluginContextOptions(new PluginContextOptions()
            .containerContext(new MavenContext()
                .project(mavenProject)));
    context = new ValueProviderContext()
        .pluginContextOptions(pluginContextOptions);
  }

  @Test
  public void testResolve() {
    String propertyName1 = getClass().getName() + "-test.prop1";
    String propertyName2 = getClass().getName() + "-test.prop2";
    String propertyName3 = getClass().getName() + "-test.prop3";

    mavenProject.getProperties().setProperty(propertyName1, "value1");
    mavenProject.getProperties().setProperty(propertyName2, "value1,value2,value3");

    assertEquals("value1", underTest.resolve(propertyName1, context));
    assertEquals(ImmutableList.of("value1", "value2", "value3"), underTest.resolve(propertyName2, context));
    assertNull(underTest.resolve(propertyName3, context));
  }

}
