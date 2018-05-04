/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2018 wcm.io
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
package io.wcm.devops.conga.tooling.maven.plugin.util;

import static io.wcm.devops.conga.tooling.maven.plugin.util.VersionInfoUtil.cleanupSnapshotVersion;
import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class VersionInfoUtilTest {

  @Test
  public void testCleanupSnapshotVersion() {
    assertEquals("1", cleanupSnapshotVersion("1"));
    assertEquals("1.2.3", cleanupSnapshotVersion("1.2.3"));
    assertEquals("1.1-SNAPSHOT", cleanupSnapshotVersion("1.1-SNAPSHOT"));
    assertEquals("2.1.2-SNAPSHOT", cleanupSnapshotVersion("2.1.2-20180125.094723-16"));
  }

}
