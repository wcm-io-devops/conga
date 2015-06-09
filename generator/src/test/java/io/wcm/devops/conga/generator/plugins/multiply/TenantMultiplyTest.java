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
package io.wcm.devops.conga.generator.plugins.multiply;

import static org.junit.Assert.assertEquals;
import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.MultiplyContext;
import io.wcm.devops.conga.generator.spi.MultiplyPlugin;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.environment.Tenant;
import io.wcm.devops.conga.model.role.Role;
import io.wcm.devops.conga.model.role.RoleFile;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;


public class TenantMultiplyTest {

  private MultiplyPlugin underTest;

  private Role role;
  private RoleFile roleFile;
  private Map<String, Object> config;
  private Environment environment;

  @Before
  public void setUp() {
    underTest = new PluginManager().get(TenantMultiply.NAME, MultiplyPlugin.class);

    role = new Role();
    roleFile = new RoleFile();
    roleFile.setFile("my_${tenant}_file");
    roleFile.setDir("my_${tenant}_dir");

    config = ImmutableMap.of("var1", "v1", "var2", "v2");

    environment = new Environment();
  }

  @Test
  public void testNoTenants() {
    List<MultiplyContext> contexts = underTest.multiply(role, roleFile, environment, config);
    assertEquals(0, contexts.size());
  }

  @Test(expected = GeneratorException.class)
  public void testTenantWithoutName() {
    environment.getTenants().add(new Tenant());

    underTest.multiply(role, roleFile, environment, config);
  }

  @Test
  public void testTenantsWithConfig() {
    Tenant tenant1 = new Tenant();
    tenant1.setTenant("tenant1");
    tenant1.setConfig(ImmutableMap.of("var1", "v11", "var3", "v33"));
    environment.getTenants().add(tenant1);

    Tenant tenant2 = new Tenant();
    tenant2.setTenant("tenant2");
    environment.getTenants().add(tenant2);

    List<MultiplyContext> contexts = underTest.multiply(role, roleFile, environment, config);
    assertEquals(2, contexts.size());

    MultiplyContext context1 = contexts.get(0);
    assertEquals("my_tenant1_file", context1.getFile());
    assertEquals("my_tenant1_dir", context1.getDir());
    assertEquals(ImmutableMap.of("var1", "v11", "var2", "v2", "var3", "v33", "tenant", "tenant1"), context1.getConfig());

    MultiplyContext context2 = contexts.get(1);
    assertEquals("my_tenant2_file", context2.getFile());
    assertEquals("my_tenant2_dir", context2.getDir());
    assertEquals(ImmutableMap.of("var1", "v1", "var2", "v2", "tenant", "tenant2"), context2.getConfig());
  }

  @Test
  public void testTenantsFilteredByRoles() {
    Tenant tenant1 = new Tenant();
    tenant1.setTenant("tenant1");
    environment.getTenants().add(tenant1);

    Tenant tenant2 = new Tenant();
    tenant2.setTenant("tenant2");
    tenant2.setRoles(ImmutableList.of("role1", "role2"));
    environment.getTenants().add(tenant2);

    Tenant tenant3 = new Tenant();
    tenant3.setTenant("tenant3");
    tenant3.setRoles(ImmutableList.of("role1"));
    environment.getTenants().add(tenant3);

    roleFile.setMultiplyOptions(ImmutableMap.of(TenantMultiply.ROLES_PROPERTY, ImmutableList.of("role1", "role3")));

    List<MultiplyContext> contexts = underTest.multiply(role, roleFile, environment, config);
    assertEquals(2, contexts.size());

    MultiplyContext context1 = contexts.get(0);
    assertEquals("my_tenant2_file", context1.getFile());

    MultiplyContext context2 = contexts.get(1);
    assertEquals("my_tenant3_file", context2.getFile());
  }

}
