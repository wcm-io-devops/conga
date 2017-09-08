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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class ValueUtilTest {

  @Test
  public void testNull() {
    assertNull(stringToValue(null));
  }

  @Test
  public void testString() {
    assertEquals("abc", stringToValue("abc"));
    assertEquals("Der Jodelkaiser aus dem Ötztal.", stringToValue("Der Jodelkaiser aus dem Ötztal."));
    assertEquals("", stringToValue(""));
  }

  @Test
  public void testStringList() {
    assertEquals(ImmutableList.of("abc", "def", "ghi"), stringToValue("abc,def,ghi"));
    assertEquals(ImmutableList.of("abc", " def", " ghi"), stringToValue("abc, def, ghi"));
  }

  @Test
  public void testInteger() {
    assertEquals(0, stringToValue("0"));
    assertEquals(123456, stringToValue("123456"));
    assertEquals(-5000, stringToValue("-5000"));
  }

  @Test
  public void testIntegerList() {
    assertEquals(ImmutableList.of(0, 123456, -5000), stringToValue("0,123456,-5000"));
  }

  @Test
  public void testLong() {
    assertEquals(0L, stringToValue("0L"));
    assertEquals(123456L, stringToValue("123456L"));
    assertEquals(1234567890123456L, stringToValue("1234567890123456"));
  }

  @Test
  public void testLongList() {
    assertEquals(ImmutableList.of(0L, 123456L, 1234567890123456L), stringToValue("0L,123456L,1234567890123456"));
  }

  @Test
  public void testDouble() {
    assertEquals(0.0f, stringToValue("0.0"));
    assertEquals(123.456, (Double)stringToValue("123.456d"), 0.0001);
  }

  @Test
  public void testBoolean() {
    assertEquals(true, stringToValue("true"));
    assertEquals(false, stringToValue("false"));
  }

  @Test
  public void testBooleanList() {
    assertEquals(ImmutableList.of(true, false), stringToValue("true,false"));
  }

  @Test
  public void testMixed() {
    assertEquals(ImmutableList.of("abc", 123, true), stringToValue("abc,123,true"));
  }

}
