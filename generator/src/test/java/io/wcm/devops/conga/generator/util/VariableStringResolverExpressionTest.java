/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2018 wcm.io
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

import io.wcm.devops.conga.generator.spi.context.PluginContextOptions;
import io.wcm.devops.conga.generator.spi.context.ValueProviderGlobalContext;

/**
 * Test {@link VariableStringResolver} with JEXL expressions.
 */
public class VariableStringResolverExpressionTest {

  private ValueProviderGlobalContext globalContext;
  private VariableStringResolver underTest;

  @Before
  public void setUp() {
    PluginContextOptions pluginContextOptions = new PluginContextOptions()
        .pluginManager(new PluginManagerImpl());
    globalContext = new ValueProviderGlobalContext()
        .pluginContextOptions(pluginContextOptions);
    underTest = new VariableStringResolver(globalContext);
  }

  @Test
  public void testSimple() {
    Map<String, Object> variables = ImmutableMap.of("var1", "v1", "var2", "v2");

    assertEquals("The v1 and v2", underTest.resolve("The ${ var1 + ' and ' + var2 }", variables));
  }

  @Test
  public void testSingleVariableWithObject() {
    Map<String, Object> variables = ImmutableMap.of("var1", ImmutableList.of("v1", "v2"));

    assertEquals(ImmutableList.of("v1", "v2"), underTest.resolve("${ var1 }", variables));
  }

  @Test
  public void testNestedVariables() {
    Map<String, Object> variables = ImmutableMap.of("var1", "v1", "var2", "${var1 + var1}", "var3", "${var2 + var1}");

    assertEquals("v1,v1v1,v1v1v1", underTest.resolve("${var1},${var2},${var3}", variables));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNestedVariables_IllegalRecursion() {
    Map<String, Object> variables = ImmutableMap.of("var1", "${ var2 }", "var2", "${ var1 }");

    underTest.resolve("${var1}", variables);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnknownVariable() {
    Map<String, Object> variables = ImmutableMap.of();

    underTest.resolve("${var1}", variables);
  }

  @Test
  public void testEscapedVariables() {
    Map<String, Object> variables = ImmutableMap.of("var1", "v1", "var2", "\\${ var1 }${ var1 }", "var3", "${ var2 }${ var1 }");

    assertEquals("${ var1 },${ var1 }v1,${ var1 }v1v1", underTest.resolve("\\${ var1 },${ var2 },${ var3 }", variables));
    assertEquals("\\${ var1 },\\${ var1 }v1,\\${ var1 }v1v1", underTest.resolve("\\${ var1 },${ var2 },${ var3 }", variables, false));
    assertEquals("${ var1 },${ var1 }v1,${ var1 }v1v1", underTest.deescape(underTest.resolveString("\\${ var1 },${ var2 },${ var3 }", variables, false)));
  }

  @Test
  public void testDeepMapVariable() {
    Map<String, Object> variables = ImmutableMap.of("var1", ImmutableMap.of("k1", "v1", "k2", ImmutableMap.of("k21", "v21", "k22", "v22")));

    assertEquals("The v1 and v21", underTest.resolve("The ${var1.k1 + ' and ' + var1.k2.k21}", variables));
  }

  @Test
  public void testListVariable() {
    Map<String, Object> variables = ImmutableMap.of("var1", ImmutableList.of("v1", "v2", ImmutableList.of("v31", "v32")));

    assertEquals("The v1,v2,v31,v32", underTest.resolve("The ${ var1 }", variables));
  }

  @Test
  public void testMapVariable() {
    Map<String, Object> variables = ImmutableMap.of("var1", ImmutableMap.of("k1", "v1", "k2", ImmutableMap.of("k21", "v21", "k22", "v22")));

    assertEquals("The k1=v1,k2=k21=v21,k22=v22", underTest.resolve("The ${ var1 }", variables));
  }

}