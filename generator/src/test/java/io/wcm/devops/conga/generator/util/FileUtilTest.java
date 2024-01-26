/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2024 wcm.io
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

class FileUtilTest {

  @Test
  void testMatchesExtension() {
    assertTrue(FileUtil.matchesExtension(new File("test.txt"), "txt"));
    assertTrue(FileUtil.matchesExtension(new File("test.part2.txt"), "txt"));
    assertFalse(FileUtil.matchesExtension(new File("test.pdf"), "txt"));
    assertTrue(FileUtil.matchesExtension(new File("test.json"), "json"));

    // special handling for OSGi configuration resource file extension
    assertTrue(FileUtil.matchesExtension(new File("test.cfg.json"), "cfg.json"));
    assertFalse(FileUtil.matchesExtension(new File("test.cfg.json"), "json"));
    assertFalse(FileUtil.matchesExtension(new File("test.json"), "cfg.json"));
  }

}
