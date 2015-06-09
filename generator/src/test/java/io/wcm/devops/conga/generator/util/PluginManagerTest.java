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
package io.wcm.devops.conga.generator.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.plugins.multiply.NoneMultiply;
import io.wcm.devops.conga.generator.plugins.multiply.TenantMultiply;
import io.wcm.devops.conga.generator.spi.MultiplyPlugin;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class PluginManagerTest {

  private PluginManager underTest;

  @Before
  public void setUp() {
    underTest = new PluginManager();
  }

  @Test
  public void testExistingPlugin() {
    MultiplyPlugin plugin = underTest.get(TenantMultiply.NAME, MultiplyPlugin.class);
    assertTrue(plugin instanceof TenantMultiply);
  }

  @Test(expected = GeneratorException.class)
  public void testNonExistingPlugin() {
    underTest.get("unknown", MultiplyPlugin.class);
  }

  @Test
  public void testGetAll() {
    List<MultiplyPlugin> plugins = underTest.getAll(MultiplyPlugin.class);
    assertEquals(2, plugins.size());

    assertTrue(plugins.get(0) instanceof NoneMultiply);
    assertTrue(plugins.get(1) instanceof TenantMultiply);
  }

}
