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
package io.wcm.devops.conga.generator.plugins.escapingstrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.wcm.devops.conga.generator.spi.EscapingStrategyPlugin;
import io.wcm.devops.conga.generator.util.PluginManager;

import org.junit.Before;
import org.junit.Test;

import com.github.jknack.handlebars.EscapingStrategy;

public class XmlEscapingStrategyTest {

  private EscapingStrategyPlugin underTest;

  @Before
  public void setUp() {
    underTest = new PluginManager().get(XmlEscapingStrategy.NAME, EscapingStrategyPlugin.class);
  }

  @Test
  public void testValid() {
    assertTrue(underTest.accepts("xml"));
    EscapingStrategy strategy = underTest.getEscapingStrategy();
    assertEquals("&quot;", strategy.escape("\""));
    assertEquals("äöüß€/", strategy.escape("äöüß€/"));
  }

  @Test
  public void testInvalid() {
    assertFalse(underTest.accepts("txt"));
  }

}
