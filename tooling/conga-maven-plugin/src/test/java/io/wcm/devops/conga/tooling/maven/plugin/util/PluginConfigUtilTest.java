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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class PluginConfigUtilTest {

  @Test
  public void testGetConfigMap() {
    assertEquals(ImmutableMap.of(),
        PluginConfigUtil.getConfigMap(null));

    assertEquals(ImmutableMap.of(
        "plugin1", ImmutableMap.of(),
        "plugin2", ImmutableMap.of()),
        PluginConfigUtil.getConfigMap("plugin1,plugin2"));

    assertEquals(ImmutableMap.of(
        "plugin1", ImmutableMap.of("param1", "abc", "param2", 5),
        "plugin2", ImmutableMap.of("param3", true)),
        PluginConfigUtil.getConfigMap("plugin1;param1=abc;param2=5,plugin2;param3=true"));
  }

}
