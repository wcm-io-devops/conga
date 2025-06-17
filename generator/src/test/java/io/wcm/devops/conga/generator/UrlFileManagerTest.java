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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.wcm.devops.conga.generator.spi.context.UrlFilePluginContext;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;

class UrlFileManagerTest {

  private UrlFileManager underTest;

  @BeforeEach
  void setUp() {
    UrlFilePluginContext context = new UrlFilePluginContext();
    underTest = new UrlFileManager(new PluginManagerImpl(), context);
  }

  @Test
  void testGetFile_Absolute() throws Exception {
    File file = new File("src/test/resources/validators/json/noJson.txt");
    try (InputStream is = underTest.getFile("file:" + file.getAbsolutePath())) {
      assertNotNull(is);
      assertTrue(IOUtils.toByteArray(is).length > 0);
    }
  }

  @Test
  void testGetFile_Relative() throws Exception {
    try (InputStream is = underTest.getFile("src/test/resources/validators/json/noJson.txt")) {
      assertNotNull(is);
      assertTrue(IOUtils.toByteArray(is).length > 0);
    }
  }

  @Test
  void testGetFile_Classpath() throws Exception {
    try (InputStream is = underTest.getFile("classpath:/validators/json/noJson.txt")) {
      assertNotNull(is);
      assertTrue(IOUtils.toByteArray(is).length > 0);
    }
  }

  @Test
  void testGetFile_Invalid() {
    assertThrows(IOException.class, () -> {
      underTest.getFile("other:/x/y/z");
    });
  }

  @Test
  void testGetFileUrl_Absolute() throws Exception {
    File file = new File("src/test/resources/validators/json/noJson.txt");
    URL url = underTest.getFileUrl("file:" + file.getAbsolutePath());
    try (InputStream is = url.openStream()) {
      assertNotNull(is);
      assertTrue(IOUtils.toByteArray(is).length > 0);
    }
  }

  @Test
  void testGetFileUrl_Relative() throws Exception {
    URL url = underTest.getFileUrl("src/test/resources/validators/json/noJson.txt");
    try (InputStream is = url.openStream()) {
      assertNotNull(is);
      assertTrue(IOUtils.toByteArray(is).length > 0);
    }
  }

  @Test
  void testGetFileUrl_Classpath() throws Exception {
    URL url = underTest.getFileUrl("classpath:/validators/json/noJson.txt");
    try (InputStream is = url.openStream()) {
      assertNotNull(is);
      assertTrue(IOUtils.toByteArray(is).length > 0);
    }
  }

  @Test
  void testGetFileUrl_Invalid() {
    assertThrows(IOException.class, () -> {
      underTest.getFileUrl("other:/x/y/z");
    });
  }

}
