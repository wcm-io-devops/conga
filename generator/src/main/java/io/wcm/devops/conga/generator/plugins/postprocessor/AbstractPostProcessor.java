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
package io.wcm.devops.conga.generator.plugins.postprocessor;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import io.wcm.devops.conga.generator.plugins.fileheader.NoneFileHeader;
import io.wcm.devops.conga.generator.spi.FileHeaderPlugin;
import io.wcm.devops.conga.generator.spi.PostProcessorPlugin;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.FileHeaderContext;
import io.wcm.devops.conga.generator.spi.context.PostProcessorContext;

/**
 * Shared functionality for post processor plugins.
 */
public abstract class AbstractPostProcessor implements PostProcessorPlugin {

  /**
   * Extract file header from given file. File type is detected automatically.
   * @param file File
   * @param postProcessorContext Post processor context
   * @return File header or null if none found
   */
  protected final FileHeaderContext extractFileHeader(FileContext file,
      PostProcessorContext postProcessorContext) {
    FileHeaderPlugin fileHeaderPlugin = detectFileHeaderPlugin(file, postProcessorContext);
    if (fileHeaderPlugin != null) {
      return fileHeaderPlugin.extract(file)
          .pluginManager(postProcessorContext.getPluginManager())
          .urlFileManager(postProcessorContext.getUrlFileManager())
          .logger(postProcessorContext.getLogger());
    }
    else {
      return null;
    }
  }

  /**
   * Apply file header to given file. File type is detected automatically.
   * @param file File
   * @param fileHeader File header
   * @param postProcessorContext Post processor context
   */
  protected final void applyFileHeader(FileContext file, FileHeaderContext fileHeader,
      PostProcessorContext postProcessorContext) {
    if (fileHeader == null || fileHeader.getCommentLines().isEmpty()) {
      return;
    }
    FileHeaderPlugin fileHeaderPlugin = detectFileHeaderPlugin(file, postProcessorContext);
    if (fileHeaderPlugin != null) {
      fileHeaderPlugin.apply(file, fileHeader);
    }
  }

  /**
   * Find matching file header plugin for the given file.
   * @param file
   * @return File header plugin or null if no match found.
   */
  private FileHeaderPlugin detectFileHeaderPlugin(FileContext file, PostProcessorContext postProcessorContext) {
    FileHeaderContext dummyFileHeader = new FileHeaderContext();
    Optional<FileHeaderPlugin> fileHeaderPlugin = postProcessorContext.getPluginManager()
        .getAll(FileHeaderPlugin.class).stream()
        .filter(plugin -> plugin.accepts(file, dummyFileHeader))
        .filter(plugin -> !StringUtils.equals(plugin.getName(), NoneFileHeader.NAME))
        .findFirst();
    if (fileHeaderPlugin.isPresent()) {
      return fileHeaderPlugin.get();
    }
    else {
      return null;
    }
  }

}
