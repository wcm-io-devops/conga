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

import io.wcm.devops.conga.generator.spi.context.ValueProviderGlobalContext;

public class VariableStringResolverTest {

  private ValueProviderGlobalContext globalContext;
  private VariableStringResolver underTest;

  @Before
  public void setUp() {
    globalContext = new ValueProviderGlobalContext().pluginManager(new PluginManagerImpl());
    underTest = new VariableStringResolver(globalContext);
  }

  @Test
  public void testSimple() {

    Map<String, Object> variables = ImmutableMap.of("var1", "v1", "var2", "v2");

    assertEquals("The v1 and v2", underTest.resolve("The ${var1} and ${var2}", variables));
  }

  @Test
  public void testSingleVariableWithObject() {

    Map<String, Object> variables = ImmutableMap.of("var1", ImmutableList.of("v1", "v2"));

    assertEquals(ImmutableList.of("v1", "v2"), underTest.resolve("${var1}", variables));
  }

  @Test
  public void testDefaultValue() {

    Map<String, Object> variables = ImmutableMap.of("var1", "v1");

    assertEquals("The v1 and theDefValue2", underTest.resolve("The ${var1:theDefValue1} and ${var2:theDefValue2}", variables));
  }

  @Test
  public void testNestedVariables() {

    Map<String, Object> variables = ImmutableMap.of("var1", "v1", "var2", "${var1}${var1}", "var3", "${var2}${var1}");

    assertEquals("v1,v1v1,v1v1v1", underTest.resolve("${var1},${var2},${var3}", variables));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNestedVariables_IllegalRecursion() {

    Map<String, Object> variables = ImmutableMap.of("var1", "${var2}", "var2", "${var1}");

    underTest.resolve("${var1}", variables);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnknownVariable() {

    Map<String, Object> variables = ImmutableMap.of();

    underTest.resolve("${var1}", variables);
  }

  @Test
  public void testEscapedVariables() {
    Map<String, Object> variables = ImmutableMap.of("var1", "v1", "var2", "\\${var1}${var1}", "var3", "${var2}${var1}");

    assertEquals("${var1},${var1}v1,${var1}v1v1", underTest.resolve("\\${var1},${var2},${var3}", variables));
    assertEquals("\\${var1},\\${var1}v1,\\${var1}v1v1", underTest.resolve("\\${var1},${var2},${var3}", variables, false));
    assertEquals("${var1},${var1}v1,${var1}v1v1", underTest.deescape(underTest.resolveString("\\${var1},${var2},${var3}", variables, false)));
  }

  @Test
  public void testEscapedVariables_Provider_DefValue() {
    Map<String, Object> variables = ImmutableMap.of("var1", "v1", "var2", "\\${var1}${var1}", "var3", "${var2}${var1}");

    assertEquals("\\${provider::var1},\\${var2:defValue},\\${provider::var3:defValue}",
        underTest.resolve("\\${provider::var1},\\${var2:defValue},\\${provider::var3:defValue}", variables, false));
    assertEquals("${provider::var1},${var2:defValue},${provider::var3:defValue}",
        underTest.deescape("\\${provider::var1},\\${var2:defValue},\\${provider::var3:defValue}"));
  }

  @Test
  public void testDeepMapVariable() {
    Map<String, Object> variables = ImmutableMap.of("var1", ImmutableMap.of("k1", "v1", "k2", ImmutableMap.of("k21", "v21", "k22", "v22")));

    assertEquals("The v1 and v21", underTest.resolve("The ${var1.k1} and ${var1.k2.k21}", variables));
  }

  @Test
  public void testListVariable() {
    Map<String, Object> variables = ImmutableMap.of("var1", ImmutableList.of("v1", "v2", ImmutableList.of("v31", "v32")));

    assertEquals("The v1,v2,v31,v32", underTest.resolve("The ${var1}", variables));
  }

  @Test
  public void testMapVariable() {
    Map<String, Object> variables = ImmutableMap.of("var1", ImmutableMap.of("k1", "v1", "k2", ImmutableMap.of("k21", "v21", "k22", "v22")));

    assertEquals("The k1=v1,k2=k21=v21,k22=v22", underTest.resolve("The ${var1}", variables));
  }

  @Test
  public void testValueProvider() {
    String propertyName1 = getClass().getName() + "-test.prop1";
    String propertyName2 = getClass().getName() + "-test.prop2";
    System.setProperty(propertyName1, "value1");

    Map<String, Object> variables = ImmutableMap.of("var1", "v1");

    assertEquals("The v1 and value1", underTest.resolve("The ${var1} and ${system::" + propertyName1 + "}", variables));
    assertEquals("The v1 and theDefValue", underTest.resolve("The ${var1} and ${system::" + propertyName2 + ":theDefValue}", variables));

    System.clearProperty(propertyName1);
    System.clearProperty(propertyName2);
  }

  @Test
  public void testCustomValueProvider() {
    // define value provider name 'customProvider' of type 'system'
    globalContext.valueProviderConfig(ImmutableMap.of("customProvider", ImmutableMap.of(ValueProviderGlobalContext.PARAM_PLUGIN_NAME, "system")));

    String propertyName1 = getClass().getName() + "-test.propCustom1";
    String propertyName2 = getClass().getName() + "-test.propCustom2";
    System.setProperty(propertyName1, "value1");

    Map<String, Object> variables = ImmutableMap.of("var1", "v1");

    assertEquals("The v1 and value1", underTest.resolve("The ${var1} and ${customProvider::" + propertyName1 + "}", variables));
    assertEquals("The v1 and theDefValue", underTest.resolve("The ${var1} and ${customProvider::" + propertyName2 + ":theDefValue}", variables));

    System.clearProperty(propertyName1);
    System.clearProperty(propertyName2);
  }

}
