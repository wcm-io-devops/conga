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

import static io.wcm.devops.conga.generator.plugins.handlebars.helper.MockOptions.FN_RETURN;
import static io.wcm.devops.conga.generator.plugins.handlebars.helper.TestUtils.assertHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import io.wcm.devops.conga.generator.spi.handlebars.HelperPlugin;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;

class ContainsHelperTest {

  private HelperPlugin<Object> helper;

  @SuppressWarnings("unchecked")
  @BeforeEach
  void setUp() {
    helper = new PluginManagerImpl().get(ContainsHelper.NAME, HelperPlugin.class);
  }

  @Test
  void testContains() throws Exception {
    assertHelper(FN_RETURN, helper, ImmutableList.of("a", "b", "c"), new MockOptions("a"));
    assertHelper(FN_RETURN, helper, ImmutableSet.of("a", "b", "c"), new MockOptions("b"));
    assertHelper(FN_RETURN, helper, new String[] { "a", "b", "c" }, new MockOptions("a"));
    assertHelper(FN_RETURN, helper, new String[] { "a", "b", "c" }, new MockOptions("b"));
  }

  @Test
  void testNotContains() throws Exception {
    assertHelper("", helper, ImmutableList.of("a", "b", "c"), new MockOptions("z"));
    assertHelper("", helper, new String[] { "a", "b", "c" }, new MockOptions("z"));
  }

  @Test
  void testNull() throws Exception {
    assertHelper("", helper, null, new MockOptions("def"));
    assertHelper("", helper, ImmutableList.of("a", "b", "c"), new MockOptions());
    assertHelper("", helper, null, new MockOptions());
  }

}
