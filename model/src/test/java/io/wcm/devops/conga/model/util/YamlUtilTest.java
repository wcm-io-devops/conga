/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2023 wcm.io
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
package io.wcm.devops.conga.model.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

class YamlUtilTest {

  @Test
  void testLoadSmallYamlFile() throws IOException {
    Yaml yaml = new Yaml(YamlUtil.createLoaderOptions());
    try (InputStream is = YamlUtilTest.class.getResourceAsStream("/role.yaml")) {
      assertNotNull(yaml.load(is));
    }
  }

  @Test
  void testLoadHugeYamlFile() throws IOException {
    Yaml yaml = new Yaml(YamlUtil.createLoaderOptions());
    try (InputStream is = YamlUtilTest.class.getResourceAsStream("/hugefile.yaml")) {
      assertNotNull(yaml.load(is));
    }
  }

}
