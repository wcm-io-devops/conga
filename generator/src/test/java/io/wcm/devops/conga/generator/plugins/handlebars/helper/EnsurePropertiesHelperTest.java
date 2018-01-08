/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 wcm.io
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
package io.wcm.devops.conga.generator.plugins.handlebars.helper;

import static io.wcm.devops.conga.generator.plugins.handlebars.helper.TestUtils.assertHelper;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import io.wcm.devops.conga.generator.spi.handlebars.HelperPlugin;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;

public class EnsurePropertiesHelperTest {

  private HelperPlugin<Object> helper;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    helper = new PluginManagerImpl().get(EnsurePropertiesHelper.NAME, HelperPlugin.class);
  }

  @Test
  public void testSet() throws Exception {

    assertHelper(null, helper, "p1", new MockOptions()
        .withProperty("p1", "v1"));

    assertHelper(null, helper, "p1", new MockOptions("p2","p3")
        .withProperty("p1", "v1")
        .withProperty("p2", "v2")
        .withProperty("p3", "v3"));

    assertHelper(null, helper, null, new MockOptions());
  }

  @Test(expected = IOException.class)
  public void testNotSetCase1() throws Exception {
    assertHelper(null, helper, "p1", new MockOptions());
  }

  @Test(expected = IOException.class)
  public void testNotSetCase2() throws Exception {
    assertHelper(null, helper, "p1", new MockOptions("p2", "p3"));
  }

  @Test(expected = IOException.class)
  public void testNotSetCase3() throws Exception {
    assertHelper(null, helper, "p1", new MockOptions("p2", "p3")
        .withProperty("p1", "v1")
        .withProperty("p2", "v2"));
  }

  @Test(expected = IOException.class)
  public void testNotSetCase4() throws Exception {
    assertHelper(null, helper, "p1", new MockOptions("p2", "p3")
        .withProperty("p2", "v1"));
  }

}
