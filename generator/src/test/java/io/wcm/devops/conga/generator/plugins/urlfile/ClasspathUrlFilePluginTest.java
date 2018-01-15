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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import io.wcm.devops.conga.generator.spi.UrlFilePlugin;
import io.wcm.devops.conga.generator.spi.context.UrlFilePluginContext;

public class ClasspathUrlFilePluginTest {

  private UrlFilePlugin underTest;
  private UrlFilePluginContext context;

  @Before
  public void setUp() {
    underTest = new ClasspathUrlFilePlugin();
    context = new UrlFilePluginContext();
  }

  @Test
  public void testAccepts() {
    assertTrue(underTest.accepts("classpath:/x/y/z", context));
    assertFalse(underTest.accepts("/x/y/z", context));
    assertFalse(underTest.accepts("other:/x/y/z", context));
  }

  @Test
  public void testGetFile() throws Exception {
    try (InputStream is = underTest.getFile("classpath:/validators/json/noJson.txt", context)) {
      assertNotNull(is);
      assertTrue(IOUtils.toByteArray(is).length > 0);
    }
  }

  @Test(expected = FileNotFoundException.class)
  public void testGetFile_NonExisting() throws Exception {
    underTest.getFile("classpath:/non-exixting-file", context);
  }

  @Test
  public void testGetFileUrl() throws Exception {
    URL url = underTest.getFileUrl("classpath:/validators/json/noJson.txt", context);
    try (InputStream is = url.openStream()) {
      assertNotNull(is);
      assertTrue(IOUtils.toByteArray(is).length > 0);
    }
  }

  @Test(expected = FileNotFoundException.class)
  public void testGetFileUrl_NonExisting() throws Exception {
    underTest.getFileUrl("classpath:/non-exixting-file", context);
  }

}
