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

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class VariableMapResolverTest {

  private VariableMapResolver underTest;

  @Before
  public void setUp() {
    underTest = new VariableMapResolver(new PluginManagerImpl());
  }

  @Test
  public void testSimple() {
    Map<String, Object> map = ImmutableMap.of("var1", "v1", "var2", 2,
        "key1", "The ${var1} and ${var2}");

    assertEquals(ImmutableMap.of("var1", "v1", "var2", 2,
        "key1", "The v1 and 2"), underTest.resolve(map));
  }

  @Test
  public void testNested() {
    Map<String, Object> map = ImmutableMap.of("var1", "v1", "var2", "${var1}v2", "var3", "${var1}${var2}v3",
        "key1", "The ${var1} and ${var2} and ${var3}");

    assertEquals(ImmutableMap.of("var1", "v1", "var2", "v1v2", "var3", "v1v1v2v3",
        "key1", "The v1 and v1v2 and v1v1v2v3"), underTest.resolve(map));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNestedCyclicReference() {
    Map<String, Object> map = ImmutableMap.of("var1", "${var2}", "var2", "${var1}");

    underTest.resolve(map);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnknownVariables() {
    Map<String, Object> map = ImmutableMap.of("key1", "The ${var1} and ${var2}");

    underTest.resolve(map);
  }

  @Test
  public void testNestedMap() {
    Map<String, Object> map = ImmutableMap.of("var1", "v1",
        "var2", ImmutableMap.of("var21", "v21", "var22", ImmutableMap.of("var221", "${var1}${var2.var21}")),
        "key1", "The ${var1} and ${var2.var22.var221}");

    assertEquals(ImmutableMap.of("var1", "v1",
        "var2", ImmutableMap.of("var21", "v21", "var22", ImmutableMap.of("var221", "v1v21")),
        "key1", "The v1 and v1v21"), underTest.resolve(map));
  }

  @Test
  public void testNestedList() {
    Map<String, Object> map = ImmutableMap.of("var1", "v1",
        "var2", ImmutableList.of("v21", ImmutableMap.of("var221", "${var1}")),
        "key1", "The ${var1} and ${var2}");

    assertEquals(ImmutableMap.of("var1", "v1",
        "var2", ImmutableList.of("v21", ImmutableMap.of("var221", "v1")),
        "key1", "The v1 and v21,var221=v1"), underTest.resolve(map));
  }

  @Test
  public void testNestedWithEscapedVariable() {
    Map<String, Object> map = ImmutableMap.of("var1", "\\${novar}", "var2", "${var1}v2", "var3", "${var1}${var2}v3",
        "key1", "The ${var1} and ${var2} and ${var3}");

    assertEquals(ImmutableMap.of("var1", "${novar}", "var2", "${novar}v2", "var3", "${novar}${novar}v2v3",
        "key1", "The ${novar} and ${novar}v2 and ${novar}${novar}v2v3"), underTest.resolve(map));
  }

}
