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

import static io.wcm.devops.conga.generator.TestUtils.TEST_VERSION;
import static io.wcm.devops.conga.generator.TestUtils.assertContains;
import static io.wcm.devops.conga.generator.TestUtils.assertDirectory;
import static io.wcm.devops.conga.generator.TestUtils.assertFile;
import static io.wcm.devops.conga.generator.TestUtils.assertNotFile;
import static io.wcm.devops.conga.generator.TestUtils.setupGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GeneratorTest {

  private Generator underTest;
  private File destDir;

  @BeforeEach
  public void setUp() throws IOException {
    destDir = new File("target/generation-test/" + getClass().getSimpleName());
    FileUtils.deleteDirectory(destDir);
    underTest = setupGenerator(destDir);
    underTest.generate(new String[0]);
  }

  @Test
  public void testAllEnvironments() {
    File node1Dir = assertDirectory(destDir, "env1/node1");

    File text1 = assertFile(node1Dir, "text/test-role1.variant11.env1.node1.txt");
    assertContains(text1, "textfile äöüß with ISO-8859-1 encoding", StandardCharsets.ISO_8859_1);
    assertContains(text1, "defaultString: \"value1\" äöüß", StandardCharsets.ISO_8859_1);
    assertContains(text1, "globalString: globalFromRole1", StandardCharsets.ISO_8859_1);
    assertContains(text1, "variableString: \\QThe v1-role1-variant11${novar}\\E", StandardCharsets.ISO_8859_1);
    assertContains(text1, "\r\n", StandardCharsets.ISO_8859_1);

    assertContains(text1, ContextProperties.ENVIRONMENT + ": env1", StandardCharsets.ISO_8859_1);
    assertContains(text1, ContextProperties.NODE + ": node1", StandardCharsets.ISO_8859_1);
    assertContains(text1, ContextProperties.ROLE + ": role1", StandardCharsets.ISO_8859_1);
    assertContains(text1, ContextProperties.ROLE_VARIANT + ": variant1", StandardCharsets.ISO_8859_1);
    assertContains(text1, ContextProperties.NODES + ": node1,node2,node3", StandardCharsets.ISO_8859_1);
    assertContains(text1, ContextProperties.NODES_BY_ROLE + ": node1,node2,node3", StandardCharsets.ISO_8859_1);
    assertContains(text1, ContextProperties.NODES_BY_ROLE_VARIANT + ": node1", StandardCharsets.ISO_8859_1);
    assertContains(text1, ContextProperties.TENANTS + ": tenant1,tenant2,tenant3_TenantSuffix", StandardCharsets.ISO_8859_1);
    assertContains(text1, ContextProperties.TENANTS_BY_ROLE + ": tenant1,tenant2", StandardCharsets.ISO_8859_1);

    assertContains(text1, "listParamFromValueProvider: [v1, v2, v3]", StandardCharsets.ISO_8859_1);
    assertContains(text1, "listParamJoined: v1|v2|v3", StandardCharsets.ISO_8859_1);

    File json1 = assertFile(node1Dir, "json/test.json");
    assertContains(json1, "JSON file äöüß€ with UTF-8 encoding");
    assertContains(json1, "\"defaultString\": \"value2\"");
    assertContains(json1, "\"globalString\": \"globalValue äöüß€\"");
    assertContains(json1, TEST_VERSION);
    assertContains(json1, "\"partialDefaultString\": \"value2\"");

    File xml1tenant1 = assertFile(node1Dir, "xml/000_test.tenant1.tenantRole1,tenantRole2.env1.xml");
    assertContains(xml1tenant1, "XML file äöüß€ with UTF-8 encoding for tenant1");
    assertContains(xml1tenant1, "<defaultString value=\"&quot;value1&quot; äöüß€\"/>");
    assertContains(xml1tenant1, "<globalString>globalValue äöüß€</globalString>");
    assertContains(xml1tenant1, "<variableString>The v1-role1-variant11${novar}</variableString>");
    assertContains(json1, TEST_VERSION);

    File xml1tenant2 = assertFile(node1Dir, "xml/001_test.tenant2.tenantRole1.env1.xml");
    assertContains(xml1tenant2, "XML file äöüß€ with UTF-8 encoding for tenant2");
    assertContains(xml1tenant2, "<defaultString value=\"defaultFromTenant2\"/>");
    assertContains(xml1tenant2, "<globalString>globalFromTenant2</globalString>");
    assertContains(xml1tenant2, "<variableString>The v1-tenant2${novar}</variableString>");

    File sample1a = assertFile(node1Dir, "files/sample.txt");
    assertContains(sample1a, "This is an example text file: äöüß€");

    File sample1b = assertFile(node1Dir, "files/sample-filesystem.txt");
    assertContains(sample1b, "This is an example text file: äöüß€");

    File sample1c = assertFile(node1Dir, "text/test-superrole1.txt");
    assertContains(sample1c, "This is a textfile äöüß with ISO-8859-1 encoding.", StandardCharsets.ISO_8859_1);

    File node2Dir = assertDirectory(destDir, "env1/node2");

    File xml2tenant1 = assertFile(node2Dir, "xml/000_test.tenant1.tenantRole1,tenantRole2.env1.xml");
    assertContains(xml2tenant1, "XML file äöüß€ with UTF-8 encoding for tenant1");
    assertContains(xml2tenant1, "<defaultString value=\"defaultFromNode2Role1\"/>");
    assertContains(xml2tenant1, "<globalString>globalValue äöüß€</globalString>");
    assertContains(xml2tenant1, "<variableString>The v1-node2${novar}</variableString>");

    File xml2tenant2 = assertFile(node2Dir, "xml/001_test.tenant2.tenantRole1.env1.xml");
    assertContains(xml2tenant2, "XML file äöüß€ with UTF-8 encoding for tenant2");
    assertContains(xml2tenant2, "<defaultString value=\"defaultFromTenant2\"/>");
    assertContains(xml2tenant2, "<globalString>globalFromTenant2</globalString>");
    assertContains(xml2tenant2, "<variableString>The v1-tenant2${novar}</variableString>");

    File node3Dir = assertDirectory(destDir, "env1/node3");
    assertFile(node3Dir, "xml/000_test.tenant1.tenantRole1,tenantRole2.env1.xml");

    // check conditional files
    assertFile(node1Dir, "text/test-conditional-tenant1.txt");
    assertNotFile(node1Dir, "text/test-conditional-tenant2.txt");
    assertNotFile(node1Dir, "text/test-conditional-tenant3_TenantSuffix.txt");
    assertFile(node2Dir, "text/test-conditional-tenant1.txt");
    assertFile(node2Dir, "text/test-conditional-tenant2.txt");
    assertNotFile(node2Dir, "text/test-conditional-tenant3_TenantSuffix.txt");

    // check list param merging
    assertContains(text1, "listParam: e1,e2,e3,e4,e0", StandardCharsets.ISO_8859_1);
    File node1Tenant1ConditionalText = assertFile(node1Dir, "text/test-conditional-tenant1.txt");
    assertContains(node1Tenant1ConditionalText, "listParam: e1,e2,e3,e4,e0", StandardCharsets.ISO_8859_1);
    File node2Tenant1ConditionalText = assertFile(node2Dir, "text/test-conditional-tenant1.txt");
    assertContains(node2Tenant1ConditionalText, "listParam: e1,e2,e0", StandardCharsets.ISO_8859_1);
    File node2Tenant2ConditionalText = assertFile(node2Dir, "text/test-conditional-tenant2.txt");
    assertContains(node2Tenant2ConditionalText, "listParam: e5,e6,e1,e2,e0", StandardCharsets.ISO_8859_1);

    // check variables defined for multiple variants
    File node4Dir = assertDirectory(destDir, "env1/node4");
    File xml4tenant2 = assertFile(node4Dir, "xml/001_test.tenant2.tenantRole1.env1.xml");
    assertContains(xml4tenant2, "<var1>v1-tenant2</var1>");
    assertContains(xml4tenant2, "<var2>v2-role1-variant11</var2>");
    assertContains(xml4tenant2, "<var3>v3-role1-variant11</var3>");
    assertContains(xml4tenant2, "<var4>v4-role1-variant13</var4>");
    assertContains(xml4tenant2, "<var5>v5-role1-variant11</var5>");

    File node5Dir = assertDirectory(destDir, "env1/node5");
    File xml5tenant2 = assertFile(node5Dir, "xml/001_test.tenant2.tenantRole1.env1.xml");
    assertContains(xml5tenant2, "<var1>v1-tenant2</var1>");
    assertContains(xml5tenant2, "<var2>v2-role1-variant13</var2>");
    assertContains(xml5tenant2, "<var3>v3-role1-variant11</var3>");
    assertContains(xml5tenant2, "<var4>v4-role1-variant13</var4>");
    assertContains(xml4tenant2, "<var5>v5-role1-variant11</var5>");

  }

}
