/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2016 wcm.io
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;

import io.wcm.devops.conga.generator.util.FileUtil;
import io.wcm.devops.conga.resource.ResourceCollection;
import io.wcm.devops.conga.resource.ResourceLoader;

public final class TestUtils {

  public static final String TEST_VERSION = "testVersion1ForFileHeader";
  public static final String TEST_DEPENDENCY_VERSION = "testVersion2ForFileHeader";

  private TestUtils() {
    // static methods only
  }

  public static Generator setupGenerator(File destDir) {
    ResourceLoader resourceLoader = new ResourceLoader();
    ResourceCollection baseDir = resourceLoader.getResourceCollection("src/test/definitions");
    Generator underTest = new Generator(
        ImmutableList.of(resourceLoader.getResourceCollection(baseDir, "roles")),
        ImmutableList.of(resourceLoader.getResourceCollection(baseDir, "templates")),
        ImmutableList.of(resourceLoader.getResourceCollection(baseDir, "environments")),
        destDir);
    underTest.setVersion(TEST_VERSION);
    underTest.setDependencyVersions(ImmutableList.of(TEST_DEPENDENCY_VERSION));
    return underTest;
  }

  public static File assertDirectory(File assertBaseDir, String path) {
    File dir = new File(assertBaseDir, path);
    assertTrue("Directory does not exist: " + FileUtil.getCanonicalPath(dir), dir.exists() && dir.isDirectory());
    return dir;
  }

  public static File assertFile(File assertBaseDir, String path) {
    File file = new File(assertBaseDir, path);
    assertTrue("File does not exist: " + FileUtil.getCanonicalPath(file), file.exists() && file.isFile());
    return file;
  }

  public static void assertNotFile(File assertBaseDir, String path) {
    File file = new File(assertBaseDir, path);
    assertFalse("File does exist: " + FileUtil.getCanonicalPath(file), file.exists() && file.isFile());
  }

  public static void assertContains(File file, String contains) {
    assertContains(file, contains, CharEncoding.UTF_8);
  }

  public static void assertContains(File file, String contains, String charset) {
    try {
      String fileContent = FileUtils.readFileToString(file, charset);
      assertTrue("File " + FileUtil.getCanonicalPath(file) + " does not contain: " + contains, StringUtils.contains(fileContent, contains));
    }
    catch (IOException ex) {
      throw new RuntimeException("Unable to read contents from: " + FileUtil.getCanonicalPath(file), ex);
    }
  }

}
