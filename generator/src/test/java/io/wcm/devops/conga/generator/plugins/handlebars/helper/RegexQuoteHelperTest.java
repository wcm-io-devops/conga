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
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import io.wcm.devops.conga.generator.spi.handlebars.HelperPlugin;
import io.wcm.devops.conga.generator.util.PluginManager;

import org.junit.Before;
import org.junit.Test;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

public class RegexQuoteHelperTest {

  private Helper<Object> helper;
  private Options options;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    helper = new PluginManager().get(RegexQuoteHelper.NAME, HelperPlugin.class);

    options = mock(Options.class);
  }

  @Test
  public void testApply() throws Exception {
    when(options.isFalsy(anyObject())).thenReturn(false);

    assertEquals("\\Qabc\\E", helper.apply("abc", options));
    assertEquals("\\Qa.b.c\\E", helper.apply("a.b.c", options));
  }

  @Test
  public void testApplyFalsy() throws Exception {
    when(options.isFalsy(anyObject())).thenReturn(true);

    when(options.param(0, null)).thenReturn(null);
    assertNull(helper.apply(null, options));

    when(options.param(0, null)).thenReturn("abc");
    assertEquals("abc", helper.apply("abc", options));

    when(options.param(0, null)).thenReturn("a.b.c");
    assertEquals("a.b.c", helper.apply("a.b.c", options));
  }

}
