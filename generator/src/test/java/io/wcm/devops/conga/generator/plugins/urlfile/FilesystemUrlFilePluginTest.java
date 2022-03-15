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
package io.wcm.devops.conga.generator.plugins.urlfile;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.UrlFilePlugin;
import io.wcm.devops.conga.generator.spi.context.UrlFilePluginContext;

class FilesystemUrlFilePluginTest {

  private UrlFilePlugin underTest;
  private UrlFilePluginContext context;

  @BeforeEach
  void setUp() {
    underTest = new FilesystemUrlFilePlugin();
    context = new UrlFilePluginContext();
  }

  @Test
  void testAccepts() {
    assertTrue(underTest.accepts("file:/x/y/z", context));
    assertFalse(underTest.accepts("/x/y/z", context));
    assertFalse(underTest.accepts("other:/x/y/z", context));
  }

  @Test
  void testAbsoluteFile() throws Exception {
    File file = new File("src/test/resources/validators/json/noJson.txt");
    try (InputStream is = underTest.getFile("file:" + file.getAbsolutePath(), context)) {
      assertNotNull(is);
      assertTrue(IOUtils.toByteArray(is).length > 0);
    }
  }

  @Test
  void testRelativeFile() throws Exception {
    try (InputStream is = underTest.getFile("src/test/resources/validators/json/noJson.txt", context)) {
      assertNotNull(is);
      assertTrue(IOUtils.toByteArray(is).length > 0);
    }
  }

  @Test
  void testRelativeFileNode() throws Exception {
    context.baseNodeDir(new File("src/test/resources/validators"));
    try (InputStream is = underTest.getFile("file-node:json/noJson.txt", context)) {
      assertNotNull(is);
      assertTrue(IOUtils.toByteArray(is).length > 0);
    }
  }

  @Test
  void testRelativeFileNode_NoNodeBaseDir() throws Exception {
    assertThrows(GeneratorException.class, () -> {
      underTest.getFile("file-node:json/noJson.txt", context);
    });
  }

  @Test
  void testGetFile_NonExisting() throws Exception {
    assertThrows(FileNotFoundException.class, () -> {
      underTest.getFile("file:non-existing-file", context);
    });
  }

  @Test
  void testGetFileUrl() throws Exception {
    File file = new File("src/test/resources/validators/json/noJson.txt");
    URL url = underTest.getFileUrl("file:" + file.getAbsolutePath(), context);
    try (InputStream is = url.openStream()) {
      assertNotNull(is);
      assertTrue(IOUtils.toByteArray(is).length > 0);
    }
  }

  @Test
  void testGetFileUrl_NonExisting() throws Exception {
    assertThrows(FileNotFoundException.class, () -> {
      underTest.getFileUrl("file:non-existing-file", context);
    });
  }

}
