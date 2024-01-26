/*-
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2015 - 2018 wcm.io DevOps
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

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.wcm.devops.conga.generator.spi.handlebars.HelperPlugin;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;

class IfNotEqualsHelperTest {

  private HelperPlugin<Object> helper;

  @SuppressWarnings("unchecked")
  @BeforeEach
  void setUp() {
    helper = new PluginManagerImpl().get(IfNotEqualsHelper.NAME, HelperPlugin.class);
  }

  @Test
  void testEquals() throws Exception {
    assertHelper("", helper, "abc", new MockOptions("abc"));
    assertHelper("", helper, List.of("a", "b", "c"), new MockOptions(List.of("a", "b", "c")));
  }

  @Test
  void testNotEquals() throws Exception {
    assertHelper(FN_RETURN, helper, "abc", new MockOptions("def"));
    assertHelper(FN_RETURN, helper, List.of("a", "b", "c"), new MockOptions(List.of("d", "e", "f")));
  }

  @Test
  void testNull() throws Exception {
    assertHelper("", helper, null, new MockOptions("def"));
    assertHelper("", helper, "abc", new MockOptions());
    assertHelper("", helper, null, new MockOptions());
  }

}
