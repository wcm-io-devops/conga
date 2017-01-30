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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;

import com.github.jknack.handlebars.Template;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.wcm.devops.conga.generator.spi.PostProcessorPlugin;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.PostProcessorContext;
import io.wcm.devops.conga.generator.spi.context.UrlFilePluginContext;
import io.wcm.devops.conga.generator.spi.export.context.GeneratedFileContext;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.model.role.RoleFile;

@RunWith(MockitoJUnitRunner.class)
public class FileGeneratorTest {

  private File destDir;
  private File file;
  private RoleFile roleFile;
  private UrlFileManager urlFileManager;
  private FileGenerator underTest;
  private Map<String, PostProcessorPlugin> postProcessorPlugins = new HashMap<>();

  @Mock
  private Template template;
  @Mock
  private PluginManager pluginManager;
  @Mock
  private Logger logger;

  @Before
  public void setUp() {
    destDir = new File("target/generation-test/" + getClass().getSimpleName());
    file = new File(destDir, "test.txt");
    roleFile = new RoleFile();
    UrlFilePluginContext urlFilePluginContext = new UrlFilePluginContext();
    urlFileManager = new UrlFileManager(pluginManager, urlFilePluginContext);

    when(pluginManager.getAll(PostProcessorPlugin.class)).thenReturn(ImmutableList.copyOf(postProcessorPlugins.values()));
    when(pluginManager.get(anyString(), eq(PostProcessorPlugin.class))).thenAnswer(new Answer<PostProcessorPlugin>() {
      @Override
      public PostProcessorPlugin answer(InvocationOnMock invocation) throws Throwable {
        return postProcessorPlugins.get(invocation.getArgument(0));
      }
    });

    underTest = new FileGenerator("env1", "role1", "variant1", "template1",
        destDir, file, null, roleFile, ImmutableMap.<String, Object>of(),
        template, pluginManager, urlFileManager,
        "1.0", ImmutableList.<String>of(), logger);
  }

  @Test
  public void testWithoutPostProcessors() throws Exception {
    List<GeneratedFileContext> result = ImmutableList.copyOf(underTest.generate());

    assertEquals(1, result.size());
    assertItem(result.get(0), "test.txt", ImmutableMap.of(), ImmutableSet.of());
  }

  @Test
  public void testOnePostProcessors() throws Exception {
    PostProcessorPlugin one = mockPostProcessor("one");
    roleFile.setPostProcessors(ImmutableList.of("one"));

    List<GeneratedFileContext> result = ImmutableList.copyOf(underTest.generate());

    assertEquals(1, result.size());
    assertItem(result.get(0), "test.txt", ImmutableMap.of(), ImmutableSet.of("one"));

    verify(one, times(1)).apply(any(FileContext.class), any(PostProcessorContext.class));
  }

  @Test
  public void testOnePostProcessorsWithRewrite() throws Exception {
    PostProcessorPlugin one = mockPostProcessor("one");
    when(one.apply(any(FileContext.class), any(PostProcessorContext.class))).thenAnswer(new Answer<List<FileContext>>() {
      @Override
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
  }

  @Test
  public void testTwoPostProcessors() throws Exception {
    PostProcessorPlugin one = mockPostProcessor("one");
    PostProcessorPlugin two = mockPostProcessor("two");
    roleFile.setPostProcessors(ImmutableList.of("one", "two"));

    List<GeneratedFileContext> result = ImmutableList.copyOf(underTest.generate());

    assertEquals(1, result.size());
    assertItem(result.get(0), "test.txt", ImmutableMap.of(), ImmutableSet.of("one", "two"));

    verify(one, times(1)).apply(any(FileContext.class), any(PostProcessorContext.class));
    verify(two, times(1)).apply(any(FileContext.class), any(PostProcessorContext.class));
  }

  @Test
  public void testTwoPostProcessorsWithRewrite() throws Exception {
    PostProcessorPlugin one = mockPostProcessor("one");
    when(one.apply(any(FileContext.class), any(PostProcessorContext.class))).thenAnswer(new Answer<List<FileContext>>() {
      @Override
      public List<FileContext> answer(InvocationOnMock invocation) throws Throwable {
        // delete input file and create new file test.abc instead
        FileContext input = invocation.getArgument(0);
        assertItem(input, "test.txt", ImmutableMap.of());
        input.getFile().delete();
        return ImmutableList.of(newFile("test.abc"));
      }
    });

    PostProcessorPlugin two = mockPostProcessor("two");
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

  private void assertItem(GeneratedFileContext item, String expectedFileName, Map<String, Object> expectedModelOptions,
      Set<String> expectedPostProcessors) {
    assertItem(item.getFileContext(), expectedFileName, expectedModelOptions);
    assertEquals(expectedPostProcessors, item.getPostProcessors());
  }

  private void assertItem(FileContext item, String expectedFileName, Map<String, Object> expectedModelOptions) {
    assertEquals(expectedFileName, item.getFile().getName());
    assertEquals(expectedModelOptions, item.getModelOptions());
  }

  private PostProcessorPlugin mockPostProcessor(String pluginName) {
    PostProcessorPlugin plugin = mock(PostProcessorPlugin.class);
    when(plugin.getName()).thenReturn(pluginName);
    postProcessorPlugins.put(pluginName, plugin);
    return plugin;
  }

  private FileContext newFile(String fileName) throws IOException {
    File newFile = new File(destDir, fileName);
    FileUtils.write(newFile, fileName, CharEncoding.UTF_8);
    return new FileContext().file(newFile);
  }

}
