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

import static org.junit.Assert.assertTrue;
import io.wcm.devops.conga.generator.util.FileUtil;
import io.wcm.devops.conga.resource.ResourceCollection;
import io.wcm.devops.conga.resource.ResourceLoader;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class GeneratorTest {

  private static final String TEST_VERSION = "testVersion1ForFileHeader";

  private Generator underTest;
  private ResourceCollection baseDir;
  private File destDir;

  @Before
  public void setUp() {
    ResourceLoader resourceLoader = new ResourceLoader();
    baseDir = resourceLoader.getResourceCollection("src/test/definitions");
    destDir = new File("target/generation-test");
    underTest = new Generator(
        ImmutableList.of(resourceLoader.getResourceCollection(baseDir, "roles")),
        ImmutableList.of(resourceLoader.getResourceCollection(baseDir, "templates")),
        ImmutableList.of(resourceLoader.getResourceCollection(baseDir, "environments")),
        destDir);
    underTest.setArtifactVersions(ImmutableList.of(TEST_VERSION));
  }

  @Test
  public void testAllEnvironments() {
    underTest.generate();

    File node1Dir = assertDirectory(destDir, "env1/node1");

    File text1 = assertFile(node1Dir, "text/test-role1.variant11.env1.node1.txt");
    assertContains(text1, "textfile äöüß with ISO-8859-1 encoding", CharEncoding.ISO_8859_1);
    assertContains(text1, "defaultString: \"value1\" äöüß", CharEncoding.ISO_8859_1);
    assertContains(text1, "globalString: globalFromRole1", CharEncoding.ISO_8859_1);
    assertContains(text1, "variableString: \\QThe v1-role1-variant11\\E", CharEncoding.ISO_8859_1);

    assertContains(text1, ContextProperties.ENVIRONMENT + ": env1", CharEncoding.ISO_8859_1);
    assertContains(text1, ContextProperties.NODE + ": node1", CharEncoding.ISO_8859_1);
    assertContains(text1, ContextProperties.ROLE + ": role1", CharEncoding.ISO_8859_1);
    assertContains(text1, ContextProperties.ROLE_VARIANT + ": variant1", CharEncoding.ISO_8859_1);
    assertContains(text1, ContextProperties.NODES + ": node1,node2", CharEncoding.ISO_8859_1);
    assertContains(text1, ContextProperties.NODES_BY_ROLE + ": node1,node2", CharEncoding.ISO_8859_1);
    assertContains(text1, ContextProperties.NODES_BY_ROLE_VARIANT + ": node1", CharEncoding.ISO_8859_1);
    assertContains(text1, ContextProperties.TENANTS + ": tenant1,tenant2,tenant3", CharEncoding.ISO_8859_1);
    assertContains(text1, ContextProperties.TENANTS_BY_ROLE + ": tenant1,tenant2", CharEncoding.ISO_8859_1);

    File json1 = assertFile(node1Dir, "json/test.json");
    assertContains(json1, "JSON file äöüß€ with UTF-8 encoding");
    assertContains(json1, "\"defaultString\": \"value2\"");
    assertContains(json1, "\"globalString\": \"globalValue äöüß€\"");
    assertContains(json1, TEST_VERSION);

    File xml1tenant1 = assertFile(node1Dir, "xml/test.tenant1.tenantRole1,tenantRole2.env1.xml");
    assertContains(xml1tenant1, "XML file äöüß€ with UTF-8 encoding for tenant1");
    assertContains(xml1tenant1, "<defaultString value=\"&quot;value1&quot; äöüß€\"/>");
    assertContains(xml1tenant1, "<globalString>globalValue äöüß€</globalString>");
    assertContains(xml1tenant1, "<variableString>The v1-role1-variant11</variableString>");
    assertContains(xml1tenant1, TEST_VERSION);

    File xml1tenant2 = assertFile(node1Dir, "xml/test.tenant2.tenantRole1.env1.xml");
    assertContains(xml1tenant2, "XML file äöüß€ with UTF-8 encoding for tenant2");
    assertContains(xml1tenant2, "<defaultString value=\"defaultFromTenant2\"/>");
    assertContains(xml1tenant2, "<globalString>globalFromTenant2</globalString>");
    assertContains(xml1tenant2, "<variableString>The v1-tenant2</variableString>");

    File node2Dir = assertDirectory(destDir, "env1/node2");

    File xml2tenant1 = assertFile(node2Dir, "xml/test.tenant1.tenantRole1,tenantRole2.env1.xml");
    assertContains(xml2tenant1, "XML file äöüß€ with UTF-8 encoding for tenant1");
    assertContains(xml2tenant1, "<defaultString value=\"defaultFromNode2Role1\"/>");
    assertContains(xml2tenant1, "<globalString>globalValue äöüß€</globalString>");
    assertContains(xml2tenant1, "<variableString>The v1-node2</variableString>");

    File xml2tenant2 = assertFile(node2Dir, "xml/test.tenant2.tenantRole1.env1.xml");
    assertContains(xml2tenant2, "XML file äöüß€ with UTF-8 encoding for tenant2");
    assertContains(xml2tenant2, "<defaultString value=\"defaultFromTenant2\"/>");
    assertContains(xml2tenant2, "<globalString>globalFromTenant2</globalString>");
    assertContains(xml2tenant2, "<variableString>The v1-tenant2</variableString>");
  }

  private File assertDirectory(File assertBaseDir, String path) {
    File dir = new File(assertBaseDir, path);
    assertTrue("Directory does not exist: " + FileUtil.getCanonicalPath(dir), dir.exists() && dir.isDirectory());
    return dir;
  }

  private File assertFile(File assertBaseDir, String path) {
    File file = new File(assertBaseDir, path);
    assertTrue("File does not exist: " + FileUtil.getCanonicalPath(file), file.exists() && file.isFile());
    return file;
  }

  private void assertContains(File file, String contains) {
    assertContains(file, contains, CharEncoding.UTF_8);
  }

  private void assertContains(File file, String contains, String charset) {
    try {
      String fileContent = FileUtils.readFileToString(file, charset);
      assertTrue("File " + FileUtil.getCanonicalPath(file) + " does not contain: " + contains, StringUtils.contains(fileContent, contains));
    }
    catch (IOException ex) {
      throw new RuntimeException("Unable to read contents from: " + FileUtil.getCanonicalPath(file), ex);
    }
  }

  @Test(expected = GeneratorException.class)
  public void testInvalidEnvironments() {
    underTest.generate("unknown");
  }

}
