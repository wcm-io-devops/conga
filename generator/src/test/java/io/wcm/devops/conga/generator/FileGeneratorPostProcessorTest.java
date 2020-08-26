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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;

import com.github.jknack.handlebars.Template;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.wcm.devops.conga.generator.spi.FileHeaderPlugin;
import io.wcm.devops.conga.generator.spi.ImplicitApplyOptions;
import io.wcm.devops.conga.generator.spi.PostProcessorPlugin;
import io.wcm.devops.conga.generator.spi.ValidatorPlugin;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.FileHeaderContext;
import io.wcm.devops.conga.generator.spi.context.PluginContextOptions;
import io.wcm.devops.conga.generator.spi.context.PostProcessorContext;
import io.wcm.devops.conga.generator.spi.context.UrlFilePluginContext;
import io.wcm.devops.conga.generator.spi.context.ValidatorContext;
import io.wcm.devops.conga.generator.spi.context.ValueProviderGlobalContext;
import io.wcm.devops.conga.generator.spi.export.context.GeneratedFileContext;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.generator.util.VariableMapResolver;
import io.wcm.devops.conga.model.role.RoleFile;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
public class FileGeneratorPostProcessorTest {

  private File destDir;
  private File file;
  private RoleFile roleFile;
  private UrlFileManager urlFileManager;
  private FileGenerator underTest;
  private Map<String, PostProcessorPlugin> postProcessorPlugins = new HashMap<>();
  private GeneratorOptions options;
  private PluginContextOptions pluginContextOptions;
  private VariableMapResolver variableMapResolver;

  @Mock
  private Template template;
  @Mock
  private PluginManager pluginManager;
  @Mock
  private Logger logger;

  @BeforeEach
  public void setUp(TestInfo testInfo) {
    destDir = new File("target/test-" + getClass().getSimpleName() + "-" + testInfo.getTestMethod().get().getName());
    file = new File(destDir, "test.txt");
    roleFile = new RoleFile();
    UrlFilePluginContext urlFilePluginContext = new UrlFilePluginContext();
    urlFileManager = new UrlFileManager(pluginManager, urlFilePluginContext);

    when(pluginManager.getAll(PostProcessorPlugin.class)).thenAnswer(new Answer<List<PostProcessorPlugin>>() {
      @Override
      public List<PostProcessorPlugin> answer(InvocationOnMock invocation) throws Throwable {
        return ImmutableList.copyOf(postProcessorPlugins.values());
      }
    });
    when(pluginManager.get(anyString(), eq(PostProcessorPlugin.class))).thenAnswer(new Answer<PostProcessorPlugin>() {
      @Override
      public PostProcessorPlugin answer(InvocationOnMock invocation) throws Throwable {
        return postProcessorPlugins.get(invocation.getArgument(0));
      }
    });

    options = new GeneratorOptions()
        .pluginManager(pluginManager)
        .version("1.0");
    pluginContextOptions = new PluginContextOptions()
        .pluginManager(pluginManager)
        .urlFileManager(urlFileManager)
        .logger(options.getLogger());
    variableMapResolver = new VariableMapResolver(
        new ValueProviderGlobalContext().pluginContextOptions(pluginContextOptions));
    underTest = new FileGenerator(options, "env1",
        "role1", ImmutableList.of("variant1"), "template1",
        destDir, file, null, null, roleFile, ImmutableMap.<String, Object>of(), template,
        variableMapResolver, urlFileManager, pluginContextOptions, ImmutableList.of());
  }

  @Test
  public void testWithoutPostProcessor() throws Exception {
    PostProcessorPlugin one = mockPostProcessor("one", "txt", ImplicitApplyOptions.NEVER);

    List<GeneratedFileContext> result = ImmutableList.copyOf(underTest.generate());

    assertEquals(1, result.size());
    assertItem(result.get(0), "test.txt", ImmutableMap.of(), ImmutableSet.of());

    verify(one, never()).apply(any(FileContext.class), any(PostProcessorContext.class));
  }

  @Test
  public void testOnePostProcessor() throws Exception {
    PostProcessorPlugin one = mockPostProcessor("one", "txt", ImplicitApplyOptions.NEVER);
    roleFile.setPostProcessors(ImmutableList.of("one"));

    List<GeneratedFileContext> result = ImmutableList.copyOf(underTest.generate());

    assertEquals(1, result.size());
    assertItem(result.get(0), "test.txt", ImmutableMap.of(), ImmutableSet.of("one"));

    verify(one, times(1)).apply(any(FileContext.class), any(PostProcessorContext.class));
  }

  @Test
  public void testOnePostProcessorWithRewrite() throws Exception {
    PostProcessorPlugin one = mockPostProcessor("one", "txt", ImplicitApplyOptions.NEVER);
    when(one.apply(any(FileContext.class), any(PostProcessorContext.class))).thenAnswer(new Answer<List<FileContext>>() {

      @Override
      @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
      public List<FileContext> answer(InvocationOnMock invocation) throws Throwable {
        // delete input file and create new file test.abc instead
        FileContext input = invocation.getArgument(0);
        assertItem(input, "test.txt", ImmutableMap.of());
        input.getFile().delete();
        return ImmutableList.of(newFile("test.abc"));
      }
    });
    roleFile.setPostProcessors(ImmutableList.of("one"));

    List<GeneratedFileContext> result = ImmutableList.copyOf(underTest.generate());

    assertEquals(1, result.size());
    assertItem(result.get(0), "test.abc", ImmutableMap.of(), ImmutableSet.of("one"));

    verify(one, times(1)).apply(any(FileContext.class), any(PostProcessorContext.class));
  }

  @Test
  public void testOnePostProcessorWithRewrite_WithFileHeaderAndValidator() throws Exception {
    PostProcessorPlugin one = mockPostProcessor("one", "txt", ImplicitApplyOptions.NEVER);
    when(one.apply(any(FileContext.class), any(PostProcessorContext.class))).thenAnswer(new Answer<List<FileContext>>() {
      @Override
      @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
      public List<FileContext> answer(InvocationOnMock invocation) throws Throwable {
        // delete input file and create new file test.abc instead
        FileContext input = invocation.getArgument(0);
        assertItem(input, "test.txt", ImmutableMap.of());
        input.getFile().delete();
        return ImmutableList.of(newFile("test.abc"));
      }
    });
    roleFile.setPostProcessors(ImmutableList.of("one"));

    roleFile.setPostProcessorOptions(ImmutableMap.of(
        FileGenerator.POSTPROCESSOR_KEY_FILE_HEADER, "my-fileheader",
        FileGenerator.POSTPROCESSOR_KEY_VALIDATORS, ImmutableList.of("my-validator")));
    underTest = new FileGenerator(options, "env1",
        "role1", ImmutableList.of("variant1"), "template1",
        destDir, file, null, null, roleFile, ImmutableMap.<String, Object>of(), template,
        variableMapResolver, urlFileManager, pluginContextOptions, ImmutableList.of());

    FileHeaderPlugin fileHeaderPlugin = mock(FileHeaderPlugin.class);
    when(pluginManager.get(eq("my-fileheader"), eq(FileHeaderPlugin.class))).thenAnswer(new Answer<FileHeaderPlugin>() {
      @Override
      public FileHeaderPlugin answer(InvocationOnMock invocation) throws Throwable {
        return fileHeaderPlugin;
      }
    });

    ValidatorPlugin validatorPlugin = mock(ValidatorPlugin.class);
    when(pluginManager.get(eq("my-validator"), eq(ValidatorPlugin.class))).thenAnswer(new Answer<ValidatorPlugin>() {
      @Override
      public ValidatorPlugin answer(InvocationOnMock invocation) throws Throwable {
        return validatorPlugin;
      }
    });

    List<GeneratedFileContext> result = ImmutableList.copyOf(underTest.generate());

    assertEquals(1, result.size());
    assertItem(result.get(0), "test.abc", ImmutableMap.of(), ImmutableSet.of("one"));

    verify(one, times(1)).apply(any(FileContext.class), any(PostProcessorContext.class));
    verify(fileHeaderPlugin, times(1)).apply(any(FileContext.class), any(FileHeaderContext.class));
    verify(validatorPlugin, times(1)).apply(any(FileContext.class), any(ValidatorContext.class));
  }

  @Test
  public void testTwoPostProcessors() throws Exception {
    PostProcessorPlugin one = mockPostProcessor("one", "txt", ImplicitApplyOptions.NEVER);
    PostProcessorPlugin two = mockPostProcessor("two", "txt", ImplicitApplyOptions.NEVER);
    roleFile.setPostProcessors(ImmutableList.of("one", "two"));

    List<GeneratedFileContext> result = ImmutableList.copyOf(underTest.generate());

    assertEquals(1, result.size());
    assertItem(result.get(0), "test.txt", ImmutableMap.of(), ImmutableSet.of("one", "two"));

    verify(one, times(1)).apply(any(FileContext.class), any(PostProcessorContext.class));
    verify(two, times(1)).apply(any(FileContext.class), any(PostProcessorContext.class));
  }

  @Test
  public void testTwoPostProcessorsWithRewrite() throws Exception {
    PostProcessorPlugin one = mockPostProcessor("one", "txt", ImplicitApplyOptions.NEVER);
    when(one.apply(any(FileContext.class), any(PostProcessorContext.class))).thenAnswer(new Answer<List<FileContext>>() {
      @Override
      @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
      public List<FileContext> answer(InvocationOnMock invocation) throws Throwable {
        // delete input file and create new file test.abc instead
        FileContext input = invocation.getArgument(0);
        assertItem(input, "test.txt", ImmutableMap.of());
        input.getFile().delete();
        return ImmutableList.of(newFile("test.abc"));
      }
    });

    PostProcessorPlugin two = mockPostProcessor("two", "abc", ImplicitApplyOptions.NEVER);
    when(two.apply(any(FileContext.class), any(PostProcessorContext.class))).thenAnswer(new Answer<List<FileContext>>() {
      @Override
      public List<FileContext> answer(InvocationOnMock invocation) throws Throwable {
        // create new file test.def
        FileContext input = invocation.getArgument(0);
        assertItem(input, "test.abc", ImmutableMap.of());
        return ImmutableList.of(newFile("test.def"));
      }
    });

    roleFile.setPostProcessors(ImmutableList.of("one", "two"));

    List<GeneratedFileContext> result = ImmutableList.copyOf(underTest.generate());

    assertEquals(2, result.size());
    assertItem(result.get(0), "test.abc", ImmutableMap.of(), ImmutableSet.of("one", "two"));
    assertItem(result.get(1), "test.def", ImmutableMap.of(), ImmutableSet.of("two"));
  }

  @Test
  public void testImplicitPostProcessor() throws Exception {
    PostProcessorPlugin one = mockPostProcessor("one", "txt", ImplicitApplyOptions.WHEN_UNCONFIGURED);

    List<GeneratedFileContext> result = ImmutableList.copyOf(underTest.generate());

    assertEquals(1, result.size());
    assertItem(result.get(0), "test.txt", ImmutableMap.of(), ImmutableSet.of("one"));

    verify(one, times(1)).apply(any(FileContext.class), any(PostProcessorContext.class));
  }

  @Test
  public void testAlwaysPostProcessor() throws Exception {
    PostProcessorPlugin one = mockPostProcessor("one", "txt", ImplicitApplyOptions.ALWAYS);

    List<GeneratedFileContext> result = ImmutableList.copyOf(underTest.generate());

    assertEquals(1, result.size());
    assertItem(result.get(0), "test.txt", ImmutableMap.of(), ImmutableSet.of("one"));

    verify(one, times(1)).apply(any(FileContext.class), any(PostProcessorContext.class));
  }

  @Test
  public void testPostProcessorWithRewriteAndImplicit() throws Exception {
    PostProcessorPlugin one = mockPostProcessor("one", "txt", ImplicitApplyOptions.NEVER);
    when(one.apply(any(FileContext.class), any(PostProcessorContext.class))).thenAnswer(new Answer<List<FileContext>>() {
      @Override
      public List<FileContext> answer(InvocationOnMock invocation) throws Throwable {
        // delete input file and create new file test.abc instead
        FileContext input = invocation.getArgument(0);
        assertItem(input, "test.txt", ImmutableMap.of());
        return ImmutableList.of(newFile("test.abc"));
      }
    });
    PostProcessorPlugin two = mockPostProcessor("two", "txt", ImplicitApplyOptions.ALWAYS);
    PostProcessorPlugin three = mockPostProcessor("three", "abc", ImplicitApplyOptions.ALWAYS);

    roleFile.setPostProcessors(ImmutableList.of("one"));

    List<GeneratedFileContext> result = ImmutableList.copyOf(underTest.generate());

    assertEquals(2, result.size());
    assertItem(result.get(0), "test.txt", ImmutableMap.of(), ImmutableSet.of("one", "two"));
    assertItem(result.get(1), "test.abc", ImmutableMap.of(), ImmutableSet.of("one", "three"));

    verify(one, times(1)).apply(any(FileContext.class), any(PostProcessorContext.class));
    verify(two, times(1)).apply(any(FileContext.class), any(PostProcessorContext.class));
    verify(three, times(1)).apply(any(FileContext.class), any(PostProcessorContext.class));
  }

  private void assertItem(GeneratedFileContext item, String expectedFileName, Map<String, Object> expectedModelOptions,
      Set<String> expectedPostProcessors) {
    assertItem(item.getFileContext(), expectedFileName, expectedModelOptions);
    assertEquals(expectedPostProcessors, item.getPostProcessors());
  }

  private void assertItem(FileContext item, String expectedFileName, Map<String, Object> expectedModelOptions) {
    assertEquals(expectedFileName, item.getFile().getName());
    assertEquals(expectedModelOptions, item.getModelOptions());
  }

  private PostProcessorPlugin mockPostProcessor(String pluginName, String extension, ImplicitApplyOptions implicitApply) {
    PostProcessorPlugin plugin = mock(PostProcessorPlugin.class);
    when(plugin.getName()).thenReturn(pluginName);
    when(plugin.accepts(any(FileContext.class), any(PostProcessorContext.class))).thenAnswer(new Answer<Boolean>() {
      @Override
      public Boolean answer(InvocationOnMock invocation) throws Throwable {
        FileContext input = invocation.getArgument(0);
        return StringUtils.endsWith(input.getFile().getName(), "." + extension);
      }
    });
    when(plugin.implicitApply(any(FileContext.class), any(PostProcessorContext.class))).thenReturn(implicitApply);
    postProcessorPlugins.put(pluginName, plugin);
    return plugin;
  }

  private FileContext newFile(String fileName) throws IOException {
    File newFile = new File(destDir, fileName);
    FileUtils.write(newFile, fileName, StandardCharsets.UTF_8);
    return new FileContext().file(newFile);
  }

}
