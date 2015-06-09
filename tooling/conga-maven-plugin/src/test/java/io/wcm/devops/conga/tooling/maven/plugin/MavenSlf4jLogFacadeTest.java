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
package io.wcm.devops.conga.tooling.maven.plugin;

import static io.wcm.devops.conga.tooling.maven.plugin.MavenSlf4jLogFacade.formatMessage;
import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class MavenSlf4jLogFacadeTest {

  @Test
  public void testFormatMessage() {
    assertEquals("Der", formatMessage("Der"));
    assertEquals("Der {}", formatMessage("Der {}"));
    assertEquals("Der Jodelkaiser", formatMessage("Der {}", "Jodelkaiser"));
    assertEquals("Der Jodelkaiser aus dem Ötztal", formatMessage("Der {} aus dem {}", "Jodelkaiser", "Ötztal"));
  }

}
