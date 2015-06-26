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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import io.wcm.devops.conga.generator.spi.handlebars.HelperPlugin;
import io.wcm.devops.conga.generator.util.PluginManager;

import org.junit.Before;
import org.junit.Test;

import com.github.jknack.handlebars.Helper;
import com.google.common.collect.ImmutableList;

public class IfEqualsHelperTest {

  private Helper<Object> helper;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    helper = new PluginManager().get(IfEqualsHelper.NAME, HelperPlugin.class);
  }

  @Test
  public void testEquals() throws Exception {
    assertEquals(FN_RETURN, helper.apply("abc", new MockOptions("abc")));
    assertEquals(FN_RETURN, helper.apply(ImmutableList.of("a", "b", "c"), new MockOptions(ImmutableList.of("a", "b", "c"))));
  }

  @Test
  public void testNotEquals() throws Exception {
    assertNull(helper.apply("abc", new MockOptions("def")));
    assertNull(helper.apply(ImmutableList.of("a", "b", "c"), new MockOptions(ImmutableList.of("d", "e", "f"))));
  }

  @Test
  public void testNull() throws Exception {
    assertNull(helper.apply(null, new MockOptions("def")));
    assertNull(helper.apply("abc", new MockOptions()));
    assertNull(helper.apply(null, new MockOptions()));
  }

}
