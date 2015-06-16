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
package io.wcm.devops.conga.generator.plugins.multiply;

import static org.junit.Assert.assertEquals;
import io.wcm.devops.conga.generator.spi.MultiplyPlugin;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.role.Role;
import io.wcm.devops.conga.model.role.RoleFile;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class NoneMultiplyTest {

  private MultiplyPlugin underTest;

  private Role role;
  private RoleFile roleFile;
  private Map<String, Object> config;
  private Environment environment;

  @Before
  public void setUp() {
    underTest = new PluginManager().get(NoneMultiply.NAME, MultiplyPlugin.class);

    role = new Role();
    roleFile = new RoleFile();

    config = ImmutableMap.of("var1", "v1");

    environment = new Environment();
  }

  @Test
  public void testMultiply() {
    List<Map<String, Object>> configs = underTest.multiply(role, roleFile, environment, config);

    assertEquals(1, configs.size());

    assertEquals(config, configs.get(0));
  }

}
