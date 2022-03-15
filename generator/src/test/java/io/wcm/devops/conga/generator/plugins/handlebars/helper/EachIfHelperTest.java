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

import static io.wcm.devops.conga.generator.plugins.handlebars.helper.TestUtils.assertHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.wcm.devops.conga.generator.spi.handlebars.HelperPlugin;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;

class EachIfHelperTest {

  private HelperPlugin<Object> helper;

  @SuppressWarnings("unchecked")
  @BeforeEach
  void setUp() {
    helper = new PluginManagerImpl().get(EachIfHelper.NAME, HelperPlugin.class);
  }

  @Test
  void testNull() throws Exception {
    assertHelper("", helper, null, new MockOptions());
    assertHelper("", helper, null, new MockOptions("a"));
  }

  @Test
  void testSingleValue() throws Exception {
    assertHelper("", helper, "v1", new MockOptions());
    assertHelper("", helper, "v1", new MockOptions("a"));
  }

  @Test
  void testList() throws Exception {
    assertHelper("", helper, ImmutableList.of("v1", "v2"), new MockOptions());
    assertHelper("", helper, ImmutableList.of("v1", "v2"), new MockOptions("a"));
    assertHelper("fn({a=1})", helper, ImmutableList.of(ImmutableMap.of("a", "1"), ImmutableMap.of("b", "2")), new MockOptions("a"));
    assertHelper("fn({a=1})fn({a=2})", helper, ImmutableList.of(ImmutableMap.of("a", "1"), ImmutableMap.of("a", "2")), new MockOptions("a"));
  }

  @Test
  void testListDeepMap() throws Exception {
    assertHelper("fn({a={b=1}})", helper,
        ImmutableList.of(ImmutableMap.of("a", "1"), ImmutableMap.of("a", ImmutableMap.of("b", "1"))), new MockOptions("a.b"));
  }

}
