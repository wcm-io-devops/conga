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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.wcm.devops.conga.generator.spi.context.PluginContextOptions;
import io.wcm.devops.conga.generator.spi.context.ValueProviderGlobalContext;

/**
 * Test {@link VariableStringResolver} with CONGA variable expressions, default values, value providers.
 */
class VariableStringResolverTest {

  private ValueProviderGlobalContext globalContext;
  private VariableStringResolver underTest;

  @BeforeEach
  void setUp() {
    PluginContextOptions pluginContextOptions = new PluginContextOptions()
        .pluginManager(new PluginManagerImpl());
    globalContext = new ValueProviderGlobalContext()
        .pluginContextOptions(pluginContextOptions);
    VariableMapResolver variableMapResolver = new VariableMapResolver(globalContext);
    underTest = new VariableStringResolver(globalContext, variableMapResolver);
  }

  @Test
  void testSimple() {
    Map<String, Object> variables = ImmutableMap.of("var1", "v1", "var2", "v2");

    assertEquals("The v1 and v2", underTest.resolve("The ${var1} and ${var2}", variables));
  }

  @Test
  void testSingleVariableWithObject() {
    Map<String, Object> variables = ImmutableMap.of("var1", ImmutableList.of("v1", "v2"));

    assertEquals(ImmutableList.of("v1", "v2"), underTest.resolve("${var1}", variables));
  }

  @Test
  void testDefaultValue() {
    Map<String, Object> variables = ImmutableMap.of("var1", "v1");

    assertEquals("The v1 and theDefValue2", underTest.resolve("The ${var1:theDefValue1} and ${var2:theDefValue2}", variables));
  }

  @Test
  void testDefaultEmptyValue() {
    Map<String, Object> variables = ImmutableMap.of("var1", "v1");
    assertEquals("The empty value: ''", underTest.resolve("The empty value: '${var2:}'", variables));
  }

  @Test
  void testNestedVariables() {
    Map<String, Object> variables = ImmutableMap.of("var1", "v1", "var2", "${var1}${var1}", "var3", "${var2}${var1}");

    assertEquals("v1,v1v1,v1v1v1", underTest.resolve("${var1},${var2},${var3}", variables));
  }

  @Test
  void testNestedVariables_IllegalRecursion() {
    Map<String, Object> variables = ImmutableMap.of("var1", "${var2}", "var2", "${var1}");

    assertThrows(IllegalArgumentException.class, () -> {
      underTest.resolve("${var1}", variables);
    });
  }

  @Test
  void testUnknownVariable() {
    Map<String, Object> variables = ImmutableMap.of();

    assertThrows(IllegalArgumentException.class, () -> {
      underTest.resolve("${var1}", variables);
    });
  }

  @Test
  void testEscapedVariables() {
    Map<String, Object> variables = ImmutableMap.of("var1", "v1", "var2", "\\${var1}${var1}", "var3", "${var2}${var1}");

    assertEquals("${var1},${var1}v1,${var1}v1v1", underTest.resolve("\\${var1},${var2},${var3}", variables));
    assertEquals("\\${var1},\\${var1}v1,\\${var1}v1v1", underTest.resolve("\\${var1},${var2},${var3}", variables, false));
    assertEquals("${var1},${var1}v1,${var1}v1v1", underTest.deescape(underTest.resolveString("\\${var1},${var2},${var3}", variables, false)));
  }

  @Test
  void testEscapedVariables_Provider_DefValue() {
    Map<String, Object> variables = ImmutableMap.of("var1", "v1", "var2", "\\${var1}${var1}", "var3", "${var2}${var1}");

    assertEquals("\\${provider::var1},\\${var2:defValue},\\${provider::var3:defValue}",
        underTest.resolve("\\${provider::var1},\\${var2:defValue},\\${provider::var3:defValue}", variables, false));
    assertEquals("${provider::var1},${var2:defValue},${provider::var3:defValue}",
        underTest.deescape("\\${provider::var1},\\${var2:defValue},\\${provider::var3:defValue}"));
  }

  @Test
  void testDeepMapVariable() {
    Map<String, Object> variables = ImmutableMap.of("var1", ImmutableMap.of("k1", "v1", "k2", ImmutableMap.of("k21", "v21", "k22", "v22")));

    assertEquals("The v1 and v21", underTest.resolve("The ${var1.k1} and ${var1.k2.k21}", variables));
  }

  @Test
  void testListVariable() {
    Map<String, Object> variables = ImmutableMap.of("var1", ImmutableList.of("v1", "v2", ImmutableList.of("v31", "v32")));

    assertEquals("The v1,v2,v31,v32", underTest.resolve("The ${var1}", variables));
  }

  @Test
  void testMapVariable() {
    Map<String, Object> variables = ImmutableMap.of("var1", ImmutableMap.of("k1", "v1", "k2", ImmutableMap.of("k21", "v21", "k22", "v22")));

    assertEquals("The k1=v1,k2=k21=v21,k22=v22", underTest.resolve("The ${var1}", variables));
  }

  @Test
  void testValueProvider() {
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
  void testCustomValueProvider() {
    // define value provider name 'customProvider' of type 'system'
    globalContext.getPluginContextOptions()
        .valueProviderConfig(ImmutableMap.of("customProvider", ImmutableMap.of(ValueProviderGlobalContext.PARAM_PLUGIN_NAME, "system")));

    String propertyName1 = getClass().getName() + "-test.propCustom1";
    String propertyName2 = getClass().getName() + "-test.propCustom2";
    System.setProperty(propertyName1, "value1");

    Map<String, Object> variables = ImmutableMap.of("var1", "v1");

    assertEquals("The v1 and value1", underTest.resolve("The ${var1} and ${customProvider::" + propertyName1 + "}", variables));
    assertEquals("The v1 and theDefValue", underTest.resolve("The ${var1} and ${customProvider::" + propertyName2 + ":theDefValue}", variables));

    // test with more complex expression for custom value provider (e.g. jsonpath)
    assertEquals("Value: theDefValue", underTest.resolve("Value: ${customProvider::$.jsonpathexpr:theDefValue}", variables));
    assertEquals("Value: theDefValue", underTest.resolve("Value: ${customProvider::$.jsonpathexpr[2:3]:theDefValue}", variables));

    System.clearProperty(propertyName1);
    System.clearProperty(propertyName2);
  }

  @Test
  void testDummyMapValueProvider() {
    Map<String, Object> variables = ImmutableMap.of("var1", "v1");

    assertEquals("The v1 and value1 and 5", underTest.resolve("The ${var1} and ${dummy-map::map.param1} and ${dummy-map::map.param2}", variables));
    assertEquals("The v1 and theDefValue", underTest.resolve("The ${var1} and ${dummy-map::map.paramNotDefined:theDefValue}", variables));
  }

  @Test
  void testDummyMapValueProvider_ListReturnValue() {
    assertEquals(ImmutableList.of("v1", "v2", "v3"), underTest.resolve("${dummy-map::map.listParam}", ImmutableMap.of()));
  }

  @Test
  void testHasValueProviderReference() {
    assertFalse(VariableStringResolver.hasValueProviderReference(""));
    assertFalse(VariableStringResolver.hasValueProviderReference("abc"));
    assertFalse(VariableStringResolver.hasValueProviderReference("${var1}"));
    assertFalse(VariableStringResolver.hasValueProviderReference("${var1} ${var2:default}"));

    assertTrue(VariableStringResolver.hasValueProviderReference("${provider::var1}"));
    assertTrue(VariableStringResolver.hasValueProviderReference("${var1} ${provider::var2:default}"));
  }

}
