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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import io.wcm.devops.conga.model.role.Role;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang3.CharEncoding;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class RoleReaderTest {

  private Role role;

  @Before
  public void setUp() throws IOException {
    RoleReader reader = new RoleReader();
    try (InputStream is = getClass().getResourceAsStream("/role.yaml")) {
      role = reader.read(new InputStreamReader(is, CharEncoding.UTF_8));
      assertNotNull(role);
    }
  }

  @Test
  public void testRole() {

    assertEquals(ImmutableList.of("sdlServices", "sdlImporter"), role.getVariants());

    assertEquals("templates/tomcat-sdl", role.getTemplateDirectory());

    assertEquals(ImmutableMap.of(
        "tomcat", ImmutableMap.of("port", 8080, "path", "/path/to/tomcat"),
        "jvm", ImmutableMap.of("heapspace", ImmutableMap.of("min", "512m", "max", "2048m"), "permgenspace", ImmutableMap.of("max", "256m")),
        "topologyConnectors", ImmutableList.of("http://localhost:8080/libs/sling/topology/connector")
        ), role.getConfig());

    assertEquals(ImmutableMap.of("var1", "value1"), role.getVariables());
  }

}
