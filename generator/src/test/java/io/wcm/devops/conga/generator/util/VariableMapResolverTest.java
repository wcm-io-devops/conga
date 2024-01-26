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

import static io.wcm.devops.conga.generator.util.VariableMapResolver.ITEM_VARIABLE;
import static io.wcm.devops.conga.generator.util.VariableMapResolver.LIST_VARIABLE_ITERATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.wcm.devops.conga.generator.spi.context.PluginContextOptions;
import io.wcm.devops.conga.generator.spi.context.ValueProviderGlobalContext;

class VariableMapResolverTest {

  private VariableMapResolver underTest;

  @BeforeEach
  void setUp() {
    PluginContextOptions pluginContextOptions = new PluginContextOptions()
        .pluginManager(new PluginManagerImpl());
    ValueProviderGlobalContext context = new ValueProviderGlobalContext()
        .pluginContextOptions(pluginContextOptions);
    underTest = new VariableMapResolver(context);
  }

  @Test
  void testSimple() {
    Map<String, Object> map = Map.of("var1", "v1", "var2", 2,
        "key1", "The ${var1} and ${var2}");

    assertEquals(Map.of("var1", "v1", "var2", 2,
        "key1", "The v1 and 2"), underTest.resolve(map));
  }

  @Test
  void testNested() {
    Map<String, Object> map = Map.of("var1", "v1", "var2", "${var1}v2", "var3", "${var1}${var2}v3",
        "key1", "The ${var1} and ${var2} and ${var3}");

    assertEquals(Map.of("var1", "v1", "var2", "v1v2", "var3", "v1v1v2v3",
        "key1", "The v1 and v1v2 and v1v1v2v3"), underTest.resolve(map));
  }

  @Test
  void testNestedCyclicReference() {
    Map<String, Object> map = Map.of("var1", "${var2}", "var2", "${var1}");

    assertThrows(IllegalArgumentException.class, () -> {
      underTest.resolve(map);
    });
  }

  @Test
  void testUnknownVariables() {
    Map<String, Object> map = Map.of("key1", "The ${var1} and ${var2}");

    assertThrows(IllegalArgumentException.class, () -> {
      underTest.resolve(map);
    });
  }

  @Test
  void testNestedMap() {
    Map<String, Object> map = Map.of("var1", "v1",
        "var2", Map.of("var21", "v21", "var22", Map.of("var221", "${var1}${var2.var21}")),
        "key1", "The ${var1} and ${var2.var22.var221}");

    assertEquals(Map.of("var1", "v1",
        "var2", Map.of("var21", "v21", "var22", Map.of("var221", "v1v21")),
        "key1", "The v1 and v1v21"), underTest.resolve(map));
  }

  @Test
  void testNestedList() {
    Map<String, Object> map = Map.of("var1", "v1",
        "var2", List.of("v21", Map.of("var221", "${var1}")),
        "key1", "The ${var1} and ${var2}");

    assertEquals(Map.of("var1", "v1",
        "var2", List.of("v21", Map.of("var221", "v1")),
        "key1", "The v1 and v21,var221=v1"), underTest.resolve(map));
  }

  @Test
  void testNestedWithEscapedVariable() {
    Map<String, Object> map = Map.of("var1", "\\${novar}", "var2", "${var1}v2", "var3", "${var1}${var2}v3",
        "key1", "The ${var1} and ${var2} and ${var3}");

    assertEquals(Map.of("var1", "${novar}", "var2", "${novar}v2", "var3", "${novar}${novar}v2v3",
        "key1", "The ${novar} and ${novar}v2 and ${novar}${novar}v2v3"), underTest.resolve(map));
  }

  @Test
  void testIterateDirect() {
    Map<String, Object> map = Map.of(
        "var1", "value1",
        "object1", Map.of(
            LIST_VARIABLE_ITERATE, List.of("item1", "item2", "item3"),
            "item", "${" + ITEM_VARIABLE + "}",
            "refvar1", "${var1}"));

    assertEquals(Map.of(
        "var1", "value1",
        "object1", List.of(
            Map.of("item", "item1", "refvar1", "value1"),
            Map.of("item", "item2", "refvar1", "value1"),
            Map.of("item", "item3", "refvar1", "value1"))),
        underTest.resolve(map));
  }

  @Test
  void testIterateVariable() {
    Map<String, Object> map = Map.of(
        "var1", "value1",
        "listholder", List.of(
            Map.of("name", "item1", "var2", "value21"),
            Map.of("name", "item2", "var2", "value22"),
            Map.of("name", "item3", "var2", "value23")),
        "object1", Map.of(
            LIST_VARIABLE_ITERATE, "${listholder}",
            "item", "${" + ITEM_VARIABLE + ".name}",
            "refvar1", "${var1}",
            "refvar2", "${" + ITEM_VARIABLE + ".var2}"));

    assertEquals(Map.of(
        "var1", "value1",
        "listholder", List.of(
            Map.of("name", "item1", "var2", "value21"),
            Map.of("name", "item2", "var2", "value22"),
            Map.of("name", "item3", "var2", "value23")),
        "object1", List.of(
            Map.of("item", "item1", "refvar1", "value1", "refvar2", "value21"),
            Map.of("item", "item2", "refvar1", "value1", "refvar2", "value22"),
            Map.of("item", "item3", "refvar1", "value1", "refvar2", "value23"))),
        underTest.resolve(map));
  }

  @Test
  void testIterateSingleValue() {
    Map<String, Object> map = Map.of(
        "var1", "value1",
        "object1", Map.of(LIST_VARIABLE_ITERATE, "${var1}",
            "item", "${" + ITEM_VARIABLE + "}"));
    assertEquals(Map.of(
        "var1", "value1",
        "object1", List.of(Map.of("item", "value1"))),
        underTest.resolve(map));
  }

}
