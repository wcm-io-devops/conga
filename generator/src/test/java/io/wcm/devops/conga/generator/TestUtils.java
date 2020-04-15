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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.wcm.devops.conga.generator.export.ModelExport;
import io.wcm.devops.conga.generator.plugins.valueprovider.DummyPluginConfigValueProviderPlugin;
import io.wcm.devops.conga.generator.util.FileUtil;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;

public final class TestUtils {

  public static final String TEST_VERSION = "testVersion1ForFileHeader";

  private TestUtils() {
    // static methods only
  }

  public static Generator setupGenerator(File destDir) {
    GeneratorOptions options = new GeneratorOptions()
        .baseDir(new File("."))
        .roleDir(new File("src/test/definitions/roles"))
        .templateDir(new File("src/test/definitions/templates"))
        .environmentDir(new File("src/test/definitions/environments"))
        .destDir(destDir)
        .version(TEST_VERSION)
        .pluginManager(new PluginManagerImpl())
        .genericPluginConfig(ImmutableMap.of(
            DummyPluginConfigValueProviderPlugin.NAME, ImmutableMap.of(
                "param1", "value1",
                "param2", 55,
                "param3", ImmutableMap.of("param31", "value31", "param32", "value32"))));

    ModelExport modelExport = new ModelExport();
    modelExport.setNode(ImmutableList.of("yaml"));
    options.modelExport(modelExport);

    return new Generator(options);
  }

  public static File assertDirectory(File assertBaseDir, String path) {
    File dir = new File(assertBaseDir, path);
    assertTrue(dir.exists() && dir.isDirectory(),
        "Directory does not exist: " + FileUtil.getCanonicalPath(dir));
    return dir;
  }

  public static File assertNotDirectory(File assertBaseDir, String path) {
    File dir = new File(assertBaseDir, path);
    assertFalse(dir.exists() && dir.isDirectory(),
        "Directory does exist: " + FileUtil.getCanonicalPath(dir));
    return dir;
  }

  public static File assertFile(File assertBaseDir, String path) {
    File file = new File(assertBaseDir, path);
    assertTrue(file.exists() && file.isFile(),
        "File does not exist: " + FileUtil.getCanonicalPath(file));
    return file;
  }

  public static File assertSymlink(File assertBaseDir, String path) {
    File file = new File(assertBaseDir, path);
    assertTrue(file.exists(), "File does not exist: " + FileUtil.getCanonicalPath(file));
    assertTrue(Files.isSymbolicLink(file.toPath()), "File is not a symlink: " + FileUtil.getCanonicalPath(file));
    return file;
  }

  public static void assertNotFile(File assertBaseDir, String path) {
    File file = new File(assertBaseDir, path);
    assertFalse(file.exists() && file.isFile(),
        "File does exist: " + FileUtil.getCanonicalPath(file));
  }

  public static void assertContains(File file, String contains) {
    assertContains(file, contains, StandardCharsets.UTF_8);
  }

  public static void assertContains(File file, String contains, Charset charset) {
    try {
      String fileContent = FileUtils.readFileToString(file, charset);
      assertTrue(StringUtils.contains(fileContent, contains),
          "File " + FileUtil.getCanonicalPath(file) + " does not contain: " + contains);
    }
    catch (IOException ex) {
      throw new RuntimeException("Unable to read contents from: " + FileUtil.getCanonicalPath(file), ex);
    }
  }

}
