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
package io.wcm.devops.conga.generator;

import static io.wcm.devops.conga.generator.TestUtils.setupGenerator;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

class GeneratorInvalidEnvironmentTest {

  private Generator underTest;
  private File destDir;

  @BeforeEach
  void setUp(TestInfo testInfo) {
    destDir = new File("target/test-" + getClass().getSimpleName() + "-" + testInfo.getTestMethod().get().getName());
    underTest = setupGenerator(destDir);
  }

  @Test
  void testInvalidEnvironments() {
    assertThrows(GeneratorException.class, () -> {
      underTest.generate(new String[] { "unknown" });
    });
  }

}
