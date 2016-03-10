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
package io.wcm.devops.conga.generator.plugins.fileheader;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.FileHeaderPlugin;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.FileHeaderContext;
import io.wcm.devops.conga.generator.util.FileUtil;

/**
 * Generic file header plugin implementation.
 */
public abstract class AbstractFileHeader implements FileHeaderPlugin {

  @Override
  public final Void apply(FileContext file, FileHeaderContext context) {
    String lineBreak = StringUtils.defaultString(getLineBreak());
    try {
      String content = FileUtils.readFileToString(file.getFile(), file.getCharset());

      List<String> sanitizedCommentLines;
      if (context.getCommentLines() == null) {
        sanitizedCommentLines = ImmutableList.of();
      }
      else {
        sanitizedCommentLines = context.getCommentLines().stream()
            .map(line -> sanitizeComment(line))
            .filter(line -> line != null)
            .map(line -> StringUtils.defaultString(getCommentLinePrefix()) + line + lineBreak)
            .collect(Collectors.toList());
      }

      int insertPosition = getInsertPosition(content);

      content = StringUtils.substring(content, 0, insertPosition)
          + StringUtils.defaultString(getCommentBlockStart())
          + StringUtils.join(sanitizedCommentLines, "")
          + StringUtils.defaultString(getCommentBlockEnd())
          + StringUtils.defaultString(getBlockSuffix())
          + StringUtils.substring(content, insertPosition);

      file.getFile().delete();
      FileUtils.write(file.getFile(), content, file.getCharset());
    }
    catch (IOException ex) {
      throw new GeneratorException("Unable to add file header to " + FileUtil.getCanonicalPath(file), ex);
    }
    return null;
  }

  protected String sanitizeComment(String line) {
    return line;
  }

  protected String getLineBreak() {
    return "\n";
  }

  protected String getCommentBlockStart() {
    return null;
  }

  protected String getCommentBlockEnd() {
    return null;
  }

  protected String getCommentLinePrefix() {
    return null;
  }

  protected String getBlockSuffix() {
    return null;
  }

  protected int getInsertPosition(String content) {
    return 0;
  }

}
