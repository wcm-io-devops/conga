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
package io.wcm.devops.conga.plugins.sling;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.wcm.devops.conga.generator.spi.PostProcessorPlugin;
import io.wcm.devops.conga.generator.util.PluginManager;

import java.io.File;
import java.io.FileInputStream;
import java.util.Dictionary;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.felix.cm.file.ConfigurationHandler;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

public class ProvisioningOsgiConfigPostProcessorTest {

  private PostProcessorPlugin underTest;

  @Before
  public void setUp() {
    underTest = new PluginManager().get(ProvisioningOsgiConfigPostProcessor.NAME, PostProcessorPlugin.class);
  }

  @Test
  public void testPostProcess() throws Exception {

    // prepare provisioning file
    File target = new File("target/postprocessor-test");
    if (target.exists()) {
      FileUtils.deleteDirectory(target);
    }
    File provisioningFile = new File(target, "test.txt");
    FileUtils.copyFile(new File(getClass().getResource("/validProvisioning.txt").toURI()), provisioningFile);

    // post-process
    assertTrue(underTest.accepts(provisioningFile, CharEncoding.UTF_8));
    underTest.postProcess(provisioningFile, CharEncoding.UTF_8, LoggerFactory.getLogger(ProvisioningOsgiConfigPostProcessor.class));

    // validate
    assertFalse(provisioningFile.exists());

    File mypid = new File(target, "my.pid.config");
    assertTrue(mypid.exists());

    try (FileInputStream is = new FileInputStream(mypid)) {
      Dictionary<?, ?> config = ConfigurationHandler.read(is);
      assertEquals("value1", config.get("stringProperty"));
      assertArrayEquals(new String[] {
          "v1", "v2", "v3"
      }, (String[])config.get("stringArrayProperty"));
      assertEquals(true, config.get("booleanProperty"));
      assertEquals(999999999999L, config.get("longProperty"));
    }

    File myfactory_mypid = new File(target, "my.factory-my.pid.config");
    assertTrue(myfactory_mypid.exists());

    File myfactory_mypid2 = new File(target, "mode1/my.factory-my.pid2.config");
    assertTrue(myfactory_mypid2.exists());

    File mypid2 = new File(target, "mode2/my.pid2.config");
    assertTrue(mypid2.exists());

  }

}
