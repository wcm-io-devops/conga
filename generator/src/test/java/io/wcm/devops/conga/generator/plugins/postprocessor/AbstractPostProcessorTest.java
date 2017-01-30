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
package io.wcm.devops.conga.generator.plugins.postprocessor;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import io.wcm.devops.conga.generator.plugins.fileheader.ConfFileHeader;
import io.wcm.devops.conga.generator.plugins.fileheader.JsonFileHeader;
import io.wcm.devops.conga.generator.spi.PostProcessorPlugin;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.FileHeaderContext;
import io.wcm.devops.conga.generator.spi.context.PostProcessorContext;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;

public class AbstractPostProcessorTest {

  @Test
  public void testApply() throws Exception {
    File file = new File("target/generation-test/postProcessor.json");
    FileUtils.copyFile(new File(getClass().getResource("/validators/json/validJson.json").toURI()), file);

    List<String> lines = ImmutableList.of("Der Jodelkaiser", "aus dem Oetztal", "ist wieder daheim.");
    FileHeaderContext fileHeader = new FileHeaderContext().commentLines(lines);
    FileContext fileContext = new FileContext().file(file);
    new JsonFileHeader().apply(fileContext, fileHeader);

    PostProcessorPlugin postProcessor = new DummyPostProcessor();
    PostProcessorContext postProcessorContext = new PostProcessorContext().pluginManager(new PluginManagerImpl());

    List<FileContext> result = postProcessor.apply(fileContext, postProcessorContext);

    assertEquals(1, result.size());

    FileContext newFileContext = result.get(0);
    FileHeaderContext newFileHeader = new ConfFileHeader().extract(newFileContext);

    assertEquals(fileHeader.getCommentLines(), newFileHeader.getCommentLines());

    file.delete();
    result.forEach(fc -> fc.getFile().delete());
  }

}
