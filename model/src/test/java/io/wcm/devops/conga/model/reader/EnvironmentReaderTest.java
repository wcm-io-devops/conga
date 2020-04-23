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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.constructor.ConstructorException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.environment.Node;
import io.wcm.devops.conga.model.environment.NodeRole;
import io.wcm.devops.conga.model.environment.RoleConfig;
import io.wcm.devops.conga.model.environment.Tenant;

public class EnvironmentReaderTest {

  private Environment environment;

  @BeforeEach
  public void setUp() throws IOException {
    EnvironmentReader reader = new EnvironmentReader();
    try (InputStream is = getClass().getResourceAsStream("/environment.yaml")) {
      environment = reader.read(is);
    }
    assertNotNull(environment);
  }

  @Test
  public void testEnvironment() {
    assertEquals(3, environment.getNodes().size());
    assertEquals(1, environment.getRoleConfig().size());
    assertEquals(2, environment.getTenants().size());

    assertEquals(ImmutableMap.of(
        "topologyConnectorPath", "/connector",
        "jvm", ImmutableMap.of("heapspace", ImmutableMap.of("max", "4096m")),
        "topologyConnectors", ImmutableList.of("http://host1${topologyConnectorPath}", "http://host2${topologyConnectorPath}")
        ), environment.getConfig());
  }

  @Test
  public void testNode() {
    Node node = environment.getNodes().get(0);

    assertEquals("importer", node.getNode());

    assertEquals(ImmutableMap.of("topologyConnectorPath", "/specialConnector",
        "jvm", ImmutableMap.of("heapspace", ImmutableMap.of("max", "2048m"))), node.getConfig());

    assertEquals(2, node.getRoles().size());
  }

  @Test
  public void testMultiNode() {
    Node node = environment.getNodes().get(1);
    assertEquals(ImmutableList.of("services-1", "services-2"), node.getNodes());
    assertEquals(1, node.getRoles().size());
  }

  @Test
  public void testNodeRole() {
    NodeRole role1 = environment.getNodes().get(0).getRoles().get(0);
    assertEquals("tomcat-services", role1.getRole());
    assertEquals("importer", role1.getVariant());
    assertEquals(ImmutableList.of("importer"), role1.getAggregatedVariants());
    assertEquals(ImmutableMap.of("topologyConnectors", ImmutableList.of("http://host3${topologyConnectorPath}")), role1.getConfig());

    NodeRole role2 = environment.getNodes().get(0).getRoles().get(1);
    assertEquals("tomcat-backendconnector", role2.getRole());
    assertEquals(ImmutableList.of("var1", "var2"), role2.getVariants());
    assertEquals(ImmutableList.of("var1", "var2"), role2.getAggregatedVariants());
  }

  @Test
  public void testRoleConfig() {
    RoleConfig roleConfig = environment.getRoleConfig().get(0);

    assertEquals("tomcat-backendconnector", roleConfig.getRole());

    assertEquals(ImmutableMap.of("jvm", ImmutableMap.of("heapspace", ImmutableMap.of("max", "1024m"))), roleConfig.getConfig());
  }

  @Test
  public void testTenant() {
    Tenant tenant = environment.getTenants().get(0);

    assertEquals("tenant1", tenant.getTenant());

    assertEquals(ImmutableList.of("website", "application"), tenant.getRoles());
    assertEquals(ImmutableMap.of("domain", "mysite.de", "website", ImmutableMap.of("hostname", "www.${domain}")), tenant.getConfig());
  }

  @Test
  public void testEnvironmentWithNullTenant() {
    assertThrows(ConstructorException.class, () -> {
      EnvironmentReader reader = new EnvironmentReader();
      try (InputStream is = getClass().getResourceAsStream("/environment_null_tenant.yaml")) {
        reader.read(is);
      }
    });
  }

  @Test
  public void testDependencies() {
    assertEquals(ImmutableList.of("url1", "mvn:url2"), environment.getDependencies());
  }

  @Test
  public void testPluginConfig() {
    Map<String, Map<String, Object>> pluginConfig = environment.getPluginConfig();
    assertNotNull(pluginConfig);

    Map<String, Object> config = pluginConfig.get("examplePlugin");
    assertNotNull(config);
    assertEquals("value1", config.get("pluginParam1"));
    assertEquals(25, config.get("pluginParam2"));

    assertNull(pluginConfig.get("notExistingPlugin"));
  }

}
