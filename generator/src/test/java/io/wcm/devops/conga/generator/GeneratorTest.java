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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

public class GeneratorTest {

  private Generator underTest;
  private File baseDir;
  private File destDir;

  @Before
  public void setUp() {
    baseDir = new File("src/test/definitions");
    destDir = new File("target/generation-test");
    underTest = new Generator(
        new File(baseDir, "roles"),
        new File(baseDir, "environments"),
        new File(baseDir, "templates"),
        destDir);
  }

  @Test
  public void testAllEnvironments() {
    underTest.generate();

    File node1Dir = assertDirectory(destDir, "env1/node1");

    File text1 = assertFile(node1Dir, "text/test.txt");
    assertContains(text1, "defaultString: value1 äöüß", CharEncoding.ISO_8859_1);

    File json1 = assertFile(node1Dir, "json/test.json");

    File xml1tenant1 = assertFile(node1Dir, "xml/test.tenant1.xml");
    File xml1tenant2 = assertFile(node1Dir, "xml/test.tenant2.xml");

    File node2Dir = assertDirectory(destDir, "env1/node2");

    File xml2tenant1 = assertFile(node2Dir, "xml/test.tenant1.xml");
    File xml2tenant2 = assertFile(node2Dir, "xml/test.tenant2.xml");
  }

  private File assertDirectory(File assertBaseDir, String path) {
    File dir = new File(assertBaseDir, path);
    assertTrue("Directory exists: " + FileUtil.getCanonicalPath(dir), dir.exists() && dir.isDirectory());
    return dir;
  }

  private File assertFile(File assertBaseDir, String path) {
    File file = new File(assertBaseDir, path);
    assertTrue("File exists: " + FileUtil.getCanonicalPath(file), file.exists() && file.isFile());
    return file;
  }

  private void assertContains(File file, String contains) {
    assertContains(file, contains, CharEncoding.UTF_8);
  }

  private void assertContains(File file, String contains, String charset) {
    try {
      String fileContent = FileUtils.readFileToString(file, charset);
      assertTrue("File " + FileUtil.getCanonicalPath(file) + " contains: " + contains, StringUtils.contains(fileContent, contains));
    }
    catch (IOException ex) {
      throw new RuntimeException("Unable to read contents from: " + FileUtil.getCanonicalPath(file), ex);
    }
  }

  @Test
  public void testOneEnvironments() {
    underTest.generate("env1");
  }

  @Test(expected = GeneratorException.class)
  public void testInvalidEnvironments() {
    underTest.generate("unknown");
  }

}