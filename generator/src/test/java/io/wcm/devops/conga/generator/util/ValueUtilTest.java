/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 wcm.io
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

import static io.wcm.devops.conga.generator.util.ValueUtil.stringToValue;
import static io.wcm.devops.conga.generator.util.ValueUtil.valueToString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ValueUtilTest {

  @Test
  void stringToValue_null() {
    assertNull(stringToValue(null));
  }

  @Test
  void stringToValue_string() {
    assertEquals("abc", stringToValue("abc"));
    assertEquals("Der Jodelkaiser aus dem Ötztal.", stringToValue("Der Jodelkaiser aus dem Ötztal."));
    assertEquals("", stringToValue(""));
  }

  @Test
  void stringToValue_stringList() {
    assertEquals(List.of("abc", "def", "ghi"), stringToValue("abc,def,ghi"));
    assertEquals(List.of("abc", " def", " ghi"), stringToValue("abc, def, ghi"));
  }

  @Test
  void stringToValue_integer() {
    assertEquals(0, stringToValue("0"));
    assertEquals(123456, stringToValue("123456"));
    assertEquals(-5000, stringToValue("-5000"));
  }

  @Test
  void stringToValue_integerList() {
    assertEquals(List.of(0, 123456, -5000), stringToValue("0,123456,-5000"));
  }

  @Test
  void stringToValue_long() {
    assertEquals(1234567890123456L, stringToValue("1234567890123456"));
  }

  @Test
  void stringToValue_longList() {
    assertEquals(List.of(0, 123456, 1234567890123456L), stringToValue("0,123456,1234567890123456"));
  }

  @Test
  void stringToValue_double() {
    assertEquals(0.0f, stringToValue("0.0"));
    assertEquals(123.456, ((Number)stringToValue("123.456")).doubleValue(), 0.0001);
  }

  @Test
  void stringToValue_versionNumberLookingLikeADouble() {
    assertEquals("1.20", stringToValue("1.20"));
  }

  @Test
  void stringToValue_boolean() {
    assertEquals(true, stringToValue("true"));
    assertEquals(false, stringToValue("false"));
  }

  @Test
  void stringToValue_booleanList() {
    assertEquals(List.of(true, false), stringToValue("true,false"));
  }

  @Test
  void stringToValue_mixed() {
    assertEquals(List.of("abc", 123, true), stringToValue("abc,123,true"));
  }

  @Test
  void valueToString_null() {
    assertEquals("", valueToString(null));
  }

  @Test
  void valueToString_primitives() {
    assertEquals("", valueToString(""));
    assertEquals("abc", valueToString("abc"));
    assertEquals("123", valueToString(123));
    assertEquals("1.23", valueToString(1.23));
    assertEquals("true", valueToString(true));
  }

  @Test
  void valueToString_list() {
    assertEquals("abc,123,1.23,true", valueToString(List.of("abc", 123, 1.23, true)));
  }

  @Test
  void valueToString_map() {
    assertEquals("prop1=abc,prop2=123", valueToString(Map.of("prop1", "abc", "prop2", 123)));
  }

}
