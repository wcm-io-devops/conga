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
package io.wcm.devops.conga.generator.plugins.handlebars.helper;

import static io.wcm.devops.conga.generator.plugins.handlebars.helper.MockOptions.FN_RETURN;
import static io.wcm.devops.conga.generator.plugins.handlebars.helper.TestUtils.assertHelper;

import org.junit.Before;
import org.junit.Test;

import com.github.jknack.handlebars.Helper;
import com.google.common.collect.ImmutableList;

import io.wcm.devops.conga.generator.spi.handlebars.HelperPlugin;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;

public class IfEqualsHelperTest {

  private Helper<Object> helper;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    helper = new PluginManagerImpl().get(IfEqualsHelper.NAME, HelperPlugin.class);
  }

  @Test
  public void testEquals() throws Exception {
    assertHelper(FN_RETURN, helper, "abc", new MockOptions("abc"));
    assertHelper(FN_RETURN, helper, ImmutableList.of("a", "b", "c"), new MockOptions(ImmutableList.of("a", "b", "c")));
  }

  @Test
  public void testNotEquals() throws Exception {
    assertHelper("", helper, "abc", new MockOptions("def"));
    assertHelper("", helper, ImmutableList.of("a", "b", "c"), new MockOptions(ImmutableList.of("d", "e", "f")));
  }

  @Test
  public void testNull() throws Exception {
    assertHelper("", helper, null, new MockOptions("def"));
    assertHelper("", helper, "abc", new MockOptions());
    assertHelper("", helper, null, new MockOptions());
  }

}
