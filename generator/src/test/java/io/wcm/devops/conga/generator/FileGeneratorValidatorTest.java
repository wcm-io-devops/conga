/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 wcm.io
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;

import com.github.jknack.handlebars.Template;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.wcm.devops.conga.generator.spi.ImplicitApplyOptions;
import io.wcm.devops.conga.generator.spi.ValidatorPlugin;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.PluginContextOptions;
import io.wcm.devops.conga.generator.spi.context.UrlFilePluginContext;
import io.wcm.devops.conga.generator.spi.context.ValidatorContext;
import io.wcm.devops.conga.generator.spi.context.ValueProviderGlobalContext;
import io.wcm.devops.conga.generator.spi.export.context.GeneratedFileContext;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.generator.util.VariableMapResolver;
import io.wcm.devops.conga.model.role.RoleFile;

@RunWith(MockitoJUnitRunner.Silent.class)
public class FileGeneratorValidatorTest {

  private File destDir;
  private File file;
  private RoleFile roleFile;
  private UrlFileManager urlFileManager;
  private FileGenerator underTest;
  private Map<String, ValidatorPlugin> validatorPlugins = new HashMap<>();

  @Mock
  private Template template;
  @Mock
  private PluginManager pluginManager;
  @Mock
  private Logger logger;

  @BeforeEach
  public void setUp() {
    destDir = new File("target/generation-test/" + getClass().getSimpleName());
    file = new File(destDir, "test.txt");
    roleFile = new RoleFile();
    UrlFilePluginContext urlFilePluginContext = new UrlFilePluginContext();
    urlFileManager = new UrlFileManager(pluginManager, urlFilePluginContext);

    when(pluginManager.getAll(ValidatorPlugin.class)).thenAnswer(new Answer<List<ValidatorPlugin>>() {
      @Override
      public List<ValidatorPlugin> answer(InvocationOnMock invocation) throws Throwable {
        return ImmutableList.copyOf(validatorPlugins.values());
      }
    });
    when(pluginManager.get(anyString(), eq(ValidatorPlugin.class))).thenAnswer(new Answer<ValidatorPlugin>() {
      @Override
      public ValidatorPlugin answer(InvocationOnMock invocation) throws Throwable {
        return validatorPlugins.get(invocation.getArgument(0));
      }
    });

    GeneratorOptions options = new GeneratorOptions()
        .pluginManager(pluginManager)
        .version("1.0");
    PluginContextOptions pluginContextOptions = new PluginContextOptions()
        .pluginManager(pluginManager)
        .urlFileManager(urlFileManager)
        .logger(options.getLogger());
    VariableMapResolver variableMapResolver = new VariableMapResolver(
        new ValueProviderGlobalContext().pluginContextOptions(pluginContextOptions));
    underTest = new FileGenerator(options, "env1",
        "role1", ImmutableList.of("variant1"), "template1",
        destDir, file, null, roleFile, ImmutableMap.<String, Object>of(), template,
        variableMapResolver, urlFileManager, pluginContextOptions, ImmutableList.of());
  }

  @Test
  public void testWithoutValidator() throws Exception {
    ValidatorPlugin one = mockValidator("one", "txt", ImplicitApplyOptions.NEVER);

    List<GeneratedFileContext> result = ImmutableList.copyOf(underTest.generate());

    assertEquals(1, result.size());
    assertItem(result.get(0), "test.txt");

    verify(one, never()).apply(any(FileContext.class), any(ValidatorContext.class));
  }

  @Test
  public void testOneValidator() throws Exception {
    ValidatorPlugin one = mockValidator("one", "txt", ImplicitApplyOptions.NEVER);
    roleFile.setValidators(ImmutableList.of("one"));

    List<GeneratedFileContext> result = ImmutableList.copyOf(underTest.generate());

    assertEquals(1, result.size());
    assertItem(result.get(0), "test.txt");

    verify(one, times(1)).apply(any(FileContext.class), any(ValidatorContext.class));
  }

  @Test
  public void testTwoValidators() throws Exception {
    ValidatorPlugin one = mockValidator("one", "txt", ImplicitApplyOptions.NEVER);
    ValidatorPlugin two = mockValidator("two", "txt", ImplicitApplyOptions.NEVER);
    roleFile.setValidators(ImmutableList.of("one", "two"));

    List<GeneratedFileContext> result = ImmutableList.copyOf(underTest.generate());

    assertEquals(1, result.size());
    assertItem(result.get(0), "test.txt");

    verify(one, times(1)).apply(any(FileContext.class), any(ValidatorContext.class));
    verify(two, times(1)).apply(any(FileContext.class), any(ValidatorContext.class));
  }

  @Test
  public void testImplicitValidator() throws Exception {
    ValidatorPlugin one = mockValidator("one", "txt", ImplicitApplyOptions.WHEN_UNCONFIGURED);

    List<GeneratedFileContext> result = ImmutableList.copyOf(underTest.generate());

    assertEquals(1, result.size());
    assertItem(result.get(0), "test.txt");

    verify(one, times(1)).apply(any(FileContext.class), any(ValidatorContext.class));
  }

  @Test
  public void testAlwaysValidator() throws Exception {
    ValidatorPlugin one = mockValidator("one", "txt", ImplicitApplyOptions.ALWAYS);

    List<GeneratedFileContext> result = ImmutableList.copyOf(underTest.generate());

    assertEquals(1, result.size());
    assertItem(result.get(0), "test.txt");

    verify(one, times(1)).apply(any(FileContext.class), any(ValidatorContext.class));
  }

  @Test
  public void testImplicitAndAlwaysValidator() throws Exception {
    ValidatorPlugin one = mockValidator("one", "txt", ImplicitApplyOptions.WHEN_UNCONFIGURED);
    ValidatorPlugin two = mockValidator("two", "txt", ImplicitApplyOptions.ALWAYS);

    List<GeneratedFileContext> result = ImmutableList.copyOf(underTest.generate());

    assertEquals(1, result.size());
    assertItem(result.get(0), "test.txt");

    verify(one, times(1)).apply(any(FileContext.class), any(ValidatorContext.class));
    verify(two, times(1)).apply(any(FileContext.class), any(ValidatorContext.class));
  }

  private void assertItem(GeneratedFileContext item, String expectedFileName) {
    assertEquals(expectedFileName, item.getFileContext().getFile().getName());
  }

  private ValidatorPlugin mockValidator(String pluginName, String extension, ImplicitApplyOptions implicitApply) {
    ValidatorPlugin plugin = mock(ValidatorPlugin.class);
    when(plugin.getName()).thenReturn(pluginName);
    when(plugin.accepts(any(FileContext.class), any(ValidatorContext.class))).thenAnswer(new Answer<Boolean>() {
      @Override
      public Boolean answer(InvocationOnMock invocation) throws Throwable {
        FileContext input = invocation.getArgument(0);
        return StringUtils.endsWith(input.getFile().getName(), "." + extension);
      }
    });
    when(plugin.implicitApply(any(FileContext.class), any(ValidatorContext.class))).thenReturn(implicitApply);
    validatorPlugins.put(pluginName, plugin);
    return plugin;
  }

}
