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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.wcm.devops.conga.generator.spi.UrlFilePlugin;
import io.wcm.devops.conga.generator.spi.context.UrlFilePluginContext;

public class HttpUrlFilePluginTest {

  private UrlFilePlugin underTest;
  private UrlFilePluginContext context;

  @BeforeEach
  public void setUp() {
    underTest = new HttpUrlFilePlugin();
    context = new UrlFilePluginContext();
  }

  @Test
  public void testAccepts() {
    assertTrue(underTest.accepts("http://x/y/z", context));
    assertTrue(underTest.accepts("https://x/y/z", context));
    assertFalse(underTest.accepts("/x/y/z", context));
    assertFalse(underTest.accepts("other:/x/y/z", context));
  }

  @Test
  public void testGetFile() throws Exception {
    try (InputStream is = underTest.getFile("https://wcm.io/", context)) {
      assertNotNull(is);
      assertTrue(IOUtils.toByteArray(is).length > 0);
    }
  }

  @Test
  public void testGetFileUrl() throws Exception {
    URL url = underTest.getFileUrl("https://wcm.io/", context);
    try (InputStream is = url.openStream()) {
      assertNotNull(is);
      assertTrue(IOUtils.toByteArray(is).length > 0);
    }
  }

}
