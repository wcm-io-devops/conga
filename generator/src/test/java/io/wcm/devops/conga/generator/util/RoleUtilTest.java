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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.model.role.Role;
import io.wcm.devops.conga.model.role.RoleFile;
import io.wcm.devops.conga.model.role.RoleInherit;
import io.wcm.devops.conga.model.role.RoleVariant;

public class RoleUtilTest {

  private Map<String, Role> roles;

  @Before
  public void setUp() throws Exception {
    roles = new HashMap<>();

    Role role1 = new Role();
    role1.setTemplateDir("role1Dir");
    role1.setConfig(ImmutableMap.<String, Object>of(
        "param1", "value1.1",
        "param2", 123,
        "param3", true));
    role1.setFiles(ImmutableList.of(
        buildFile("file1.1"),
        buildFile("file1.2")));
    roles.put("role1", role1);

    Role role2 = new Role();
    role2.setVariants(ImmutableList.of(
        buildVariant("variant1"),
        buildVariant("variant2", ImmutableMap.<String, Object>of(
            "varparam1", "varvalue2.1",
            "varparam2", 888))));
    role2.setTemplateDir("role2Dir");
    role2.setConfig(ImmutableMap.<String, Object>of(
        "param1", "value2.1",
        "param4", "value2.4"));
    role2.setFiles(ImmutableList.of(
        buildFile("file2.1", "variant1"),
        buildFile("file2.2", "variant2")));
    role2.setInherits(ImmutableList.of(
        buildInherit("role1")));
    roles.put("role2", role2);

    Role role3 = new Role();
    role3.setVariants(ImmutableList.of(
        buildVariant("variant1"),
        buildVariant("variant2", ImmutableMap.<String, Object>of(
            "varparam1", "varvalue3.1")),
        buildVariant("variant3", ImmutableMap.<String, Object>of(
            "varparam1", "varvalue3.2"))));
    role3.setTemplateDir("role3Dir");
    role3.setConfig(ImmutableMap.<String, Object>of(
        "param1", "value3.1",
        "param5", "value3.5"));
    role3.setFiles(ImmutableList.of(
        buildFile("file3.1", "variant1"),
        buildFile("file3.2", "variant2")));
    role3.setInherits(ImmutableList.of(
        buildInherit("role2")));
    roles.put("role3", role3);

    Role role4 = new Role();
    role4.setTemplateDir("role4Dir");
    role4.setConfig(ImmutableMap.<String, Object>of(
        "param1", "value4.1"));
    role4.setFiles(ImmutableList.of(
        buildFile("file4.1")));
    role4.setInherits(ImmutableList.of(
        buildInherit("role2")));
    roles.put("role4", role4);

    Role role5 = new Role();
    role5.setTemplateDir("role5Dir");
    role5.setConfig(ImmutableMap.<String, Object>of(
        "param1", "value5.1"));
    role5.setFiles(ImmutableList.of(
        buildFile("file5.1")));
    role5.setInherits(ImmutableList.of(
        buildInherit("role5")));
    roles.put("role5", role5);
  }

  @Test(expected = GeneratorException.class)
  public void testUnknownRole() {
    RoleUtil.resolveRole("roleX", "context", roles);
  }

  @Test
  public void testRole1() {
    Role role = RoleUtil.resolveRole("role1", "context", roles);

    assertNull(role.getTemplateDir());
    assertEquals("value1.1", role.getConfig().get("param1"));
    assertEquals(123, role.getConfig().get("param2"));
    assertEquals(true, role.getConfig().get("param3"));

    assertFile(role, "file1.1", "role1Dir");
    assertFile(role, "file1.2", "role1Dir");
  }

  @Test
  public void testRole2() {
    Role role = RoleUtil.resolveRole("role2", "context", roles);

    assertNull(role.getTemplateDir());
    assertEquals("value2.1", role.getConfig().get("param1"));
    assertEquals(123, role.getConfig().get("param2"));
    assertEquals(true, role.getConfig().get("param3"));

    assertFile(role, "file1.1", "role1Dir");
    assertFile(role, "file1.2", "role1Dir");
    assertFile(role, "file2.1", "role2Dir", "variant1");
    assertFile(role, "file2.2", "role2Dir", "variant2");

    assertVariant(role, "variant1");
    assertVariant(role, "variant2", ImmutableMap.<String, Object>of(
        "varparam1", "varvalue2.1",
        "varparam2", 888));
  }

  @Test
  public void testRole3() {
    Role role = RoleUtil.resolveRole("role3", "context", roles);

    assertNull(role.getTemplateDir());
    assertEquals("value3.1", role.getConfig().get("param1"));
    assertEquals(123, role.getConfig().get("param2"));
    assertEquals(true, role.getConfig().get("param3"));

    assertFile(role, "file1.1", "role1Dir");
    assertFile(role, "file1.2", "role1Dir");
    assertFile(role, "file2.1", "role2Dir", "variant1");
    assertFile(role, "file2.2", "role2Dir", "variant2");
    assertFile(role, "file3.1", "role3Dir", "variant1");
    assertFile(role, "file3.2", "role3Dir", "variant2");

    assertVariant(role, "variant1");
    assertVariant(role, "variant2", ImmutableMap.<String, Object>of(
        "varparam1", "varvalue3.1",
        "varparam2", 888));
    assertVariant(role, "variant3", ImmutableMap.<String, Object>of(
        "varparam1", "varvalue3.2"));
  }

  @Test(expected = GeneratorException.class)
  public void testRole4_InheritWithMissingVariants() {
    RoleUtil.resolveRole("role4", "context", roles);
  }

  @Test(expected = GeneratorException.class)
  public void testRole5_CyclicInheritance() {
    RoleUtil.resolveRole("role5", "context", roles);
  }


  // ----- test helper methods -----

  private RoleVariant buildVariant(String variantName) {
    return buildVariant(variantName, null);
  }

  private RoleVariant buildVariant(String variantName, Map<String, Object> config) {
    RoleVariant variant = new RoleVariant();
    variant.setVariant(variantName);
    variant.setConfig(config);
    return variant;
  }

  private RoleFile buildFile(String name, String... variants) {
    RoleFile file = new RoleFile();
    file.setFile(name);
    file.setTemplate(name + ".hbs");
    if (variants.length > 0) {
      file.setVariants(ImmutableList.copyOf(variants));
    }
    return file;
  }

  private RoleInherit buildInherit(String roleName, String... variants) {
    RoleInherit inherit = new RoleInherit();
    inherit.setRole(roleName);
    return inherit;
  }

  private void assertFile(Role role, String file, String templateDir, String... variants) {
    String template = FilenameUtils.concat(templateDir, file + ".hbs");
    for (RoleFile fileItem : role.getFiles()) {
      if (StringUtils.equals(file, fileItem.getFile())
          && StringUtils.equals(template, fileItem.getTemplate())
          && ImmutableList.copyOf(variants).equals(fileItem.getVariants())) {
        // item found
        return;
      }
    }
    fail("File '" + file + "' with template '" + template + "' not found.");
  }

  private void assertVariant(Role role, String variant) {
    assertVariant(role, variant, ImmutableMap.of());
  }

  private void assertVariant(Role role, String variant, Map<String, Object> config) {
    for (RoleVariant variantItem : role.getVariants()) {
      if (StringUtils.equals(variant, variantItem.getVariant())
          && config.equals(variantItem.getConfig())) {
        // item found
        return;
      }
    }
    fail("Variant '" + variant + "' with config '" + config + "' not found.");
  }

}
