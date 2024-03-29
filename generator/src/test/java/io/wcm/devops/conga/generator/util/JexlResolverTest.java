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

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.context.PluginContextOptions;
import io.wcm.devops.conga.generator.spi.context.ValueProviderGlobalContext;

class JexlResolverTest {

  private JexlResolver underTest;
  private Map<String, Object> variables;
  private Map<String, Object> object1;
  private Map<String, Object> object2;

  @BeforeEach
  void setUp() {
    PluginContextOptions pluginContextOptions = new PluginContextOptions()
        .pluginManager(new PluginManagerImpl());
    ValueProviderGlobalContext context = new ValueProviderGlobalContext()
        .pluginContextOptions(pluginContextOptions);
    VariableMapResolver variableMapResolver = new VariableMapResolver(context);

    underTest = new JexlResolver(variableMapResolver);
    object2 = Map.of("var4", "value4");
    object1 = Map.of("var3", "value3", "object2", object2,
        "nested1var1", "${nested1.nested1var1}",
        "nested2var1", "${nested1.nested2.nested2var1}");
    variables = Map.ofEntries(
        entry("var1", "value1"),
        entry("var2", 123),
        entry("object1", object1),
        entry("refVar1", "${var1}"),
        entry("refVar2", "${var2}"),
        entry("refCombined", "${object1.var3}|${var1}"),
        entry("jexlExpr", "${object1.var3 + ';' + var1}"),
        entry("nested1", Map.of(
            "nested1var1", "nested1-value1",
            "nested1JexlExpr", "${object1.var3 + ';' + var1}",
            "nested2", Map.of(
                "nested2var1", "nested2-value1",
                "nested2JexlExpr", "${object1.var3 + ';' + var1}"))),
        entry("array1", new String[] { "v1", "v2", "v3" })
    );
  }

  @Test
  void testStaticExpressions() {
    assertEquals("abc", underTest.resolve("'abc'", variables));
    assertEquals(12, underTest.resolve("12", variables));
    assertEquals(17, underTest.resolve("12+5", variables));
    assertEquals(40, underTest.resolve("12 * 3 + 4", variables));
    assertEquals(true, underTest.resolve("true", variables));
    assertEquals(false, underTest.resolve("false", variables));
  }

  @Test
  void testVariableExpressions() {
    assertEquals("value1", underTest.resolve("var1", variables));
    assertEquals("value1a", underTest.resolve("var1+'a'", variables));
    assertEquals(123, underTest.resolve("var2", variables));
    assertEquals(125, underTest.resolve("var2+2", variables));
    assertEquals("value3", underTest.resolve("object1.var3", variables));
    assertEquals("value4", underTest.resolve("object1.object2.var4", variables));
    assertEquals("nested1-value1", underTest.resolve("object1.nested1var1", variables));
    assertEquals("nested2-value1", underTest.resolve("object1.nested2var1", variables));
  }

  @Test
  void testInvalidExpressions() {
    assertThrows(GeneratorException.class, () -> {
      underTest.resolve("'abc", variables);
    });
  }

  @Test
  void testStringUtilsMethod() {
    assertEquals("v1:v2:v3", underTest.resolve("stringUtils:join(array1,':')", variables));
  }

}
