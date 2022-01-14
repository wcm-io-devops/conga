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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.wcm.devops.conga.generator.ContextProperties;
import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.MultiplyPlugin;
import io.wcm.devops.conga.generator.spi.context.MultiplyContext;
import io.wcm.devops.conga.generator.spi.context.PluginContextOptions;
import io.wcm.devops.conga.generator.spi.context.ValueProviderGlobalContext;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;
import io.wcm.devops.conga.generator.util.VariableMapResolver;
import io.wcm.devops.conga.generator.util.VariableStringResolver;
import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.environment.Tenant;
import io.wcm.devops.conga.model.role.Role;
import io.wcm.devops.conga.model.role.RoleFile;


class TenantMultiplyTest {

  private MultiplyPlugin underTest;

  private Role role;
  private RoleFile roleFile;
  private Map<String, Object> config;
  private Environment environment;
  private MultiplyContext context;

  @BeforeEach
  void setUp() {
    PluginManager pluginManager = new PluginManagerImpl();
    underTest = pluginManager.get(TenantMultiply.NAME, MultiplyPlugin.class);

    role = new Role();
    roleFile = new RoleFile();

    config = ImmutableMap.of("var1", "v1", "var2", "v2");

    environment = new Environment();

    PluginContextOptions pluginContextOptions = new PluginContextOptions()
        .pluginManager(pluginManager);
    ValueProviderGlobalContext valueProviderGlobalContext = new ValueProviderGlobalContext()
        .pluginContextOptions(pluginContextOptions);

    VariableMapResolver variableMapResolver = new VariableMapResolver(valueProviderGlobalContext);
    VariableStringResolver variableStringResolver = new VariableStringResolver(valueProviderGlobalContext, variableMapResolver);

    context = new MultiplyContext()
        .pluginContextOptions(pluginContextOptions)
        .role(role)
        .roleFile(roleFile)
        .environment(environment)
        .config(config)
        .variableStringResolver(variableStringResolver)
        .variableMapResolver(variableMapResolver);
  }

  @Test
  void testNoTenants() {
    List<Map<String, Object>> configs = underTest.multiply(context);
    assertEquals(0, configs.size());
  }

  @Test
  void testTenantWithoutName() {
    environment.getTenants().add(new Tenant());

    assertThrows(GeneratorException.class, () -> {
      underTest.multiply(context);
    });
  }

  @Test
  void testTenantsWithConfig() {
    Tenant tenant1 = new Tenant();
    tenant1.setTenant("tenant1");
    tenant1.setConfig(ImmutableMap.of("var1", "v11", "var3", "v33"));
    environment.getTenants().add(tenant1);

    Tenant tenant2 = new Tenant();
    tenant2.setTenant("tenant2");
    environment.getTenants().add(tenant2);

    List<Map<String, Object>> configs = underTest.multiply(context);
    assertEquals(2, configs.size());

    Map<String, Object> config1 = configs.get(0);
    assertEquals(ImmutableMap.of("var1", "v11", "var2", "v2", "var3", "v33",
        ContextProperties.TENANT, "tenant1",
        ContextProperties.TENANT_ROLES, ImmutableList.of()), config1);

    Map<String, Object> config2 = configs.get(1);
    assertEquals(ImmutableMap.of("var1", "v1", "var2", "v2",
        ContextProperties.TENANT, "tenant2",
        ContextProperties.TENANT_ROLES, ImmutableList.of()), config2);
  }

  @Test
  void testTenantsFilteredByRoles() {
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

    List<Map<String, Object>> configs = underTest.multiply(context);
    assertEquals(2, configs.size());

    Map<String, Object> config1 = configs.get(0);
    assertEquals(ImmutableMap.of("var1", "v1", "var2", "v2",
        ContextProperties.TENANT, "tenant2",
        ContextProperties.TENANT_ROLES, ImmutableList.of("role1", "role2")), config1);

    Map<String, Object> config2 = configs.get(1);
    assertEquals(ImmutableMap.of("var1", "v1", "var2", "v2",
        ContextProperties.TENANT, "tenant3",
        ContextProperties.TENANT_ROLES, ImmutableList.of("role1")), config2);
  }

}
