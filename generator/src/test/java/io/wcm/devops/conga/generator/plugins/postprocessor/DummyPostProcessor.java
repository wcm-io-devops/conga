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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.ImmutableList;

import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.FileHeaderContext;
import io.wcm.devops.conga.generator.spi.context.PostProcessorContext;

class DummyPostProcessor extends AbstractPostProcessor {

  @Override
  public String getName() {
    return "dummy";
  }

  @Override
  public boolean accepts(FileContext file, PostProcessorContext context) {
    return true;
  }

  @Override
  public List<FileContext> apply(FileContext file, PostProcessorContext context) {
    FileHeaderContext fileHeader = extractFileHeader(file, context);

    File newFile = new File("target/generation-test/postProcessorResult.conf");
    try {
      FileUtils.writeStringToFile(newFile, "Test Config Content", StandardCharsets.UTF_8);
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    FileContext newFileContext = new FileContext().file(newFile).charset(StandardCharsets.UTF_8);

    if (fileHeader != null) {
      applyFileHeader(newFileContext, fileHeader, context);
    }

    return ImmutableList.of(newFileContext);
  }

}
