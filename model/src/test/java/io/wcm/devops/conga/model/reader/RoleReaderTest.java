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
package io.wcm.devops.conga.model.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.wcm.devops.conga.model.role.Role;
import io.wcm.devops.conga.model.role.RoleFile;
import io.wcm.devops.conga.model.role.RoleFile.RoleFileVariantMetadata;
import io.wcm.devops.conga.model.role.RoleInherit;
import io.wcm.devops.conga.model.role.RoleVariant;
import io.wcm.devops.conga.model.shared.LineEndings;

class RoleReaderTest {

  private Role role;

  @BeforeEach
  void setUp() throws IOException {
    RoleReader reader = new RoleReader();
    try (InputStream is = getClass().getResourceAsStream("/role.yaml")) {
      role = reader.read(is);
    }
    assertNotNull(role);
  }

  @Test
  void testRole() {

    List<RoleInherit> inherits = role.getInherits();
    assertEquals(2, inherits.size());

    List<RoleVariant> variants = role.getVariants();
    assertEquals(2, variants.size());

    assertEquals("tomcat-services", role.getTemplateDir());

    List<RoleFile> files = role.getFiles();
    assertEquals(7, files.size());

    assertEquals(Map.of(
        "var1", "value1",
        "group1", Map.of("var2", "value2"),
        "tomcat", Map.of("port", 8080, "path", "/path/to/tomcat"),
        "jvm", Map.of("heapspace", Map.of("min", "512m", "max", "2048m"), "permgenspace", Map.of("max", "256m")),
        "topologyConnectors", List.of("http://localhost:8080/libs/sling/topology/connector")
    ), role.getConfig());
  }

  @Test
  void testInherit() {
    RoleInherit inherit = role.getInherits().get(0);

    assertEquals("superRole1", inherit.getRole());
  }

  @Test
  void testVariant() {
    RoleVariant variant = role.getVariants().get(0);

    assertEquals("services", variant.getVariant());
    assertEquals(Map.of("var1", "value1_service"), variant.getConfig());
  }

  @Test
  void testFile() {
    RoleFile file = role.getFiles().get(0);

    assertEquals("systemconfig-importer.txt", file.getFile());
    assertEquals("data/deploy", file.getDir());
    assertEquals("systemconfig-importer.txt.hbs", file.getTemplate());
    assertEquals(List.of("importer", "variant2*", "variant3"), file.getVariants());
    assertEquals("${abc}", file.getCondition());
    assertEquals(List.of("sling-provisioning-model"), file.getValidators());
    assertEquals(Map.of("option1", "value1"), file.getValidatorOptions());
    assertEquals(List.of("osgi-config-generator"), file.getPostProcessors());
    assertEquals(Map.of("option2", "value2"), file.getPostProcessorOptions());
    assertEquals("sling-provisioning", file.getFileHeader());
    assertEquals("none", file.getMultiply());
    assertEquals(StandardCharsets.UTF_8.name(), file.getCharset());
    assertEquals(LineEndings.windows, file.getLineEndings());
    assertEquals("none", file.getEscapingStrategy());

    RoleFile vhostFile = role.getFiles().get(4);
    assertEquals("tenant", vhostFile.getMultiply());
    assertEquals(Map.of("roles", List.of("website")), vhostFile.getMultiplyOptions());
    assertEquals(LineEndings.unix, vhostFile.getLineEndings());

    List<RoleFileVariantMetadata> variantsMetadata = file.getVariantsMetadata();
    assertEquals("importer", variantsMetadata.get(0).getVariant());
    assertFalse(variantsMetadata.get(0).isMandatory());
    assertEquals("variant2", variantsMetadata.get(1).getVariant());
    assertTrue(variantsMetadata.get(1).isMandatory());
    assertEquals("variant3", variantsMetadata.get(2).getVariant());
    assertFalse(variantsMetadata.get(2).isMandatory());
  }

  @Test
  void testDownload() {
    RoleFile file = role.getFiles().get(5);

    assertEquals("download", file.getDir());
    assertEquals("classpath://xyz.txt", file.getUrl());
    assertEquals(Map.of("modelOption1", "value1"), file.getModelOptions());
    assertTrue(file.isDeleteSource());
  }

  @Test
  void testSymlink() {
    RoleFile file = role.getFiles().get(6);

    assertEquals("systemconfig_symlink.txt", file.getFile());
    assertEquals("symlink", file.getDir());
    assertEquals("systemconfig.txt", file.getSymlinkTarget());
  }

  @Test
  void testSensitiveConfigurationParameters() {
    assertEquals(List.of("var1", "group1.var2"), role.getSensitiveConfigParameters());
  }

}
