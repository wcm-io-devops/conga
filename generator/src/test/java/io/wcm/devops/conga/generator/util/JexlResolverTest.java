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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

import io.wcm.devops.conga.generator.GeneratorException;

public class JexlResolverTest {

  private JexlResolver underTest;
  private Map<String, Object> variables;
  private Map<String, Object> object1;
  private Map<String, Object> object2;

  @BeforeEach
  public void setUp() {
    underTest = new JexlResolver();
    object2 = ImmutableMap.of("var4", "value4");
    object1 = ImmutableMap.of("var3", "value3", "object2", object2);
    variables = ImmutableMap.of("var1", "value1", "var2", 123, "object1", object1);
  }

  @Test
  public void testStaticExpressions() {
    assertEquals("abc", underTest.resolve("'abc'", variables));
    assertEquals(12, underTest.resolve("12", variables));
    assertEquals(17, underTest.resolve("12+5", variables));
    assertEquals(40, underTest.resolve("12 * 3 + 4", variables));
    assertEquals(true, underTest.resolve("true", variables));
    assertEquals(false, underTest.resolve("false", variables));
  }

  @Test
  public void testVariableExpressions() {
    assertEquals("value1", underTest.resolve("var1", variables));
    assertEquals("value1a", underTest.resolve("var1+'a'", variables));
    assertEquals(123, underTest.resolve("var2", variables));
    assertEquals(125, underTest.resolve("var2+2", variables));
    assertEquals(object1, underTest.resolve("object1", variables));
    assertEquals("value3", underTest.resolve("object1.var3", variables));
    assertEquals("value4", underTest.resolve("object1.object2.var4", variables));
  }

  @Test
  public void testInvalidExpressions() {
    assertThrows(GeneratorException.class, () -> {
      underTest.resolve("'abc", variables);
    });
  }

}
