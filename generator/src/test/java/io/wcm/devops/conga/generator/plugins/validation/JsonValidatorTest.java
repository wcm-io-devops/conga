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
package io.wcm.devops.conga.generator.plugins.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.wcm.devops.conga.generator.spi.ValidationException;
import io.wcm.devops.conga.generator.spi.ValidatorPlugin;
import io.wcm.devops.conga.generator.spi.context.ValidatorContext;
import io.wcm.devops.conga.generator.util.PluginManager;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class JsonValidatorTest {

  private ValidatorPlugin underTest;

  @Before
  public void setUp() {
    underTest = new PluginManager().get(JsonValidator.NAME, ValidatorPlugin.class);
  }

  @Test
  public void testValidJson() throws Exception {
    File file = new File(getClass().getResource("/validators/json/validJson.json").toURI());
    ValidatorContext context = new ValidatorContext().file(file);
    assertTrue(underTest.accepts(context));
    underTest.validate(context);
  }

  @Test(expected = ValidationException.class)
  public void testInvalidJson() throws Exception {
    File file = new File(getClass().getResource("/validators/json/invalidJson.json").toURI());
    ValidatorContext context = new ValidatorContext().file(file);
    assertTrue(underTest.accepts(context));
    underTest.validate(context);
  }

  @Test
  public void testNoJson() throws Exception {
    File file = new File(getClass().getResource("/validators/json/noJson.txt").toURI());
    ValidatorContext context = new ValidatorContext().file(file);
    assertFalse(underTest.accepts(context));
  }

}
