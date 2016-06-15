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
import static org.junit.Assert.assertNull;

import org.junit.Test;

import io.wcm.devops.conga.model.shared.LineEndings;

public class LineEndingConverterTest {

  @Test
  public void testNormalizeToUnix() {
    assertNull(LineEndingConverter.normalizeToUnix(null));
    assertEquals("abc", LineEndingConverter.normalizeToUnix("abc"));
    assertEquals("abc\ndef\nghi\njkl\n", LineEndingConverter.normalizeToUnix("abc\ndef\nghi\njkl\n"));
    assertEquals("abc\ndef\nghi\njkl\n", LineEndingConverter.normalizeToUnix("abc\r\ndef\r\nghi\r\njkl\r\n"));
    assertEquals("abc\ndef\nghi\njkl\n", LineEndingConverter.normalizeToUnix("abc\rdef\rghi\rjkl\r"));
    assertEquals("abc\ndef\nghi\njkl\n", LineEndingConverter.normalizeToUnix("abc\r\ndef\rghi\njkl\r\n"));
  }

  @Test
  public void testConvertToUnix() {
    assertNull(LineEndingConverter.convertTo(null, LineEndings.unix));
    assertEquals("abc", LineEndingConverter.convertTo("abc", LineEndings.unix));
    assertEquals("abc\ndef\nghi\n", LineEndingConverter.convertTo("abc\ndef\nghi\n", LineEndings.unix));
  }

  @Test
  public void testConvertToWindows() {
    assertNull(LineEndingConverter.convertTo(null, LineEndings.windows));
    assertEquals("abc", LineEndingConverter.convertTo("abc", LineEndings.windows));
    assertEquals("abc\r\ndef\r\nghi\r\n", LineEndingConverter.convertTo("abc\ndef\nghi\n", LineEndings.windows));
  }

  @Test
  public void testConvertToMacOS() {
    assertNull(LineEndingConverter.convertTo(null, LineEndings.macos));
    assertEquals("abc", LineEndingConverter.convertTo("abc", LineEndings.macos));
    assertEquals("abc\rdef\rghi\r", LineEndingConverter.convertTo("abc\ndef\nghi\n", LineEndings.macos));
  }

  @Test
  public void testConvertToNull() {
    assertNull(LineEndingConverter.convertTo(null, null));
    assertEquals("abc", LineEndingConverter.convertTo("abc", null));
    assertEquals("abc\ndef\nghi\n", LineEndingConverter.convertTo("abc\ndef\nghi\n", null));
  }

}
