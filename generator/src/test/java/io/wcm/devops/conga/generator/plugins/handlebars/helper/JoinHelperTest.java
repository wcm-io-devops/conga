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

import static org.junit.Assert.assertEquals;
import io.wcm.devops.conga.generator.spi.handlebars.HelperPlugin;
import io.wcm.devops.conga.generator.util.PluginManager;

import org.junit.Before;
import org.junit.Test;

import com.github.jknack.handlebars.Helper;
import com.google.common.collect.ImmutableList;

public class JoinHelperTest {

  private Helper<Object> helper;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    helper = new PluginManager().get(JoinHelper.NAME, HelperPlugin.class);
  }

  @Test
  public void testApply() throws Exception {
    assertEquals("a|b|c", helper.apply(ImmutableList.of("a", "b", "c"), new MockOptions("|")));
  }

}