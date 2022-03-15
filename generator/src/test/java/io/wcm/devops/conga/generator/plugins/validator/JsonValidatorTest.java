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
package io.wcm.devops.conga.generator.plugins.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.wcm.devops.conga.generator.spi.ValidationException;
import io.wcm.devops.conga.generator.spi.ValidatorPlugin;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;

class JsonValidatorTest {

  private ValidatorPlugin underTest;

  @BeforeEach
  void setUp() {
    underTest = new PluginManagerImpl().get(JsonValidator.NAME, ValidatorPlugin.class);
  }

  @Test
  void testValid() throws Exception {
    File file = new File(getClass().getResource("/validators/json/validJson.json").toURI());
    FileContext fileContext = new FileContext().file(file);
    assertTrue(underTest.accepts(fileContext, null));
    underTest.apply(fileContext, null);
  }

  @Test
  void testInvalid() throws Exception {
    File file = new File(getClass().getResource("/validators/json/invalidJson.json").toURI());
    FileContext fileContext = new FileContext().file(file);
    assertTrue(underTest.accepts(fileContext, null));

    assertThrows(ValidationException.class, () -> {
      underTest.apply(fileContext, null);
    });
  }

  @Test
  void testInvalidFileExtension() throws Exception {
    File file = new File(getClass().getResource("/validators/json/noJson.txt").toURI());
    FileContext fileContext = new FileContext().file(file);
    assertFalse(underTest.accepts(fileContext, null));
  }

}
