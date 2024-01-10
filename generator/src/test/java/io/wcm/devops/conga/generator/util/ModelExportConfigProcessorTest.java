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
package io.wcm.devops.conga.generator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import io.wcm.devops.conga.generator.spi.ValueEncryptionPlugin;
import io.wcm.devops.conga.generator.spi.context.PluginContextOptions;
import io.wcm.devops.conga.generator.spi.context.ValueEncryptionContext;

@ExtendWith(MockitoExtension.class)
class ModelExportConfigProcessorTest {

  private static final Set<String> SENSITIVE_PARAMS = Set.of(
      "param1",
      "group1.param3",
      "group1.group2.param4",
      "list1",
      "list2.param1",
      "list2.list3.param3"
      );

  @Mock
  private PluginManager pluginManager;
  @Mock
  private ValueEncryptionPlugin valueEncryptPlugin;

  private PluginContextOptions pluginContextOptions;

  private ModelExportConfigProcessor underTest;

  @BeforeEach
  void setUp() {
    // simulate valueEncryptPlugin that prepends {enc} to the value
    when(pluginManager.getAll(ValueEncryptionPlugin.class)).thenReturn(List.of(valueEncryptPlugin));
    when(valueEncryptPlugin.isEnabled()).thenReturn(true);
    when(valueEncryptPlugin.encrypt(anyString(), any(), any(ValueEncryptionContext.class))).then(new Answer<Object>() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        Object value = invocation.getArgument(1);
        return "{enc}" + value.toString();
      }
    });

    pluginContextOptions = new PluginContextOptions()
        .pluginManager(pluginManager);
    underTest = new ModelExportConfigProcessor(pluginContextOptions, SENSITIVE_PARAMS);
  }

  @Test
  void testNestedParams() {
    Map<String, Object> config = Map.<String, Object>of(
        "param1", "value1",
        "param2", "value2",
        "group1", Map.<String, Object>of(
            "param3", 123,
            "group2", Map.<String, Object>of(
                "param4", true,
                "param5", 5L)));

    Map<String, Object> expected = Map.<String, Object>of(
        "param1", "{enc}value1",
        "param2", "value2",
        "group1", Map.<String, Object>of(
            "param3", "{enc}123",
            "group2", Map.<String, Object>of(
                "param4", "{enc}true",
                "param5", 5L)));

    assertEquals(expected, underTest.apply(config));
  }

  @Test
  void testList() {
    Map<String, Object> config = Map.<String, Object>of(
        "list1", List.of("value1", "value2"),
        "list2", List.of(
            Map.<String, Object>of(
                "param1", "value1a",
                "param2", 123,
                "list3", List.of(
                    Map.of(
                        "param3", "value3"))),
            Map.<String, Object>of(
                "param1", "value1b",
                "param2", 345)));

    Map<String, Object> expected = Map.<String, Object>of(
        "list1", List.of("{enc}value1", "{enc}value2"),
        "list2", List.of(
            Map.<String, Object>of(
                "param1", "{enc}value1a",
                "param2", 123,
                "list3", List.of(
                    Map.of(
                        "param3", "{enc}value3"))),
            Map.<String, Object>of(
                "param1", "{enc}value1b",
                "param2", 345)));

    assertEquals(expected, underTest.apply(config));
  }

}

