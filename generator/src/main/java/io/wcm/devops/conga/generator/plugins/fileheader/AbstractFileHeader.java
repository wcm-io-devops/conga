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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.FileHeaderPlugin;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.FileHeaderContext;

/**
 * Generic file header plugin implementation.
 */
public abstract class AbstractFileHeader implements FileHeaderPlugin {

  @Override
  @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
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
            .map(this::sanitizeComment)
            .filter(Objects::nonNull)
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
      throw new GeneratorException("Unable to add file header to " + file.getCanonicalPath(), ex);
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

  protected int getInsertPosition(@SuppressWarnings("unused") String content) {
    return 0;
  }

  /**
   * Extract file header from the beginning of file between comment block start and end symbol.
   * @param file File
   * @return File header or null
   */
  protected final FileHeaderContext extractFileHeaderBetweenBlockStartEnd(FileContext file) {
    try {
      if (StringUtils.isNotEmpty(getCommentBlockStart()) && StringUtils.isNotEmpty(getCommentBlockEnd())) {
        String content = FileUtils.readFileToString(file.getFile(), file.getCharset());
        int insertPosition = getInsertPosition(content);
        int posBlockStart = content.indexOf(getCommentBlockStart());
        int posBlockEnd = content.indexOf(getCommentBlockEnd());
        if (posBlockStart == insertPosition && posBlockEnd > 0) {
          String fileHeader = content.substring(posBlockStart + getCommentBlockStart().length(), posBlockEnd);
          List<String> lines = ImmutableList.copyOf(StringUtils.split(fileHeader, getLineBreak()));
          return new FileHeaderContext().commentLines(lines);
        }
      }
    }
    catch (IOException ex) {
      throw new GeneratorException("Unable to parse file header from " + file.getCanonicalPath(), ex);
    }
    return null;
  }

  /**
   * Extract file header from the beginning of file with all lines starting with line prefix.
   * @param file File File
   * @return File header or null
   */
  protected final FileHeaderContext extractFileHeaderWithLinePrefixes(FileContext file) {
    try {
      if (StringUtils.isNotEmpty(getLineBreak()) && StringUtils.isNotEmpty(getCommentLinePrefix())) {
        String content = FileUtils.readFileToString(file.getFile(), file.getCharset());
        int insertPosition = getInsertPosition(content);
        content = content.substring(insertPosition);

        String[] contentLines = StringUtils.split(content, getLineBreak());
        if (contentLines.length > 0 && StringUtils.startsWith(contentLines[0], getCommentLinePrefix())) {
          List<String> lines = new ArrayList<>();
          for (int i = 0; i < contentLines.length; i++) {
            if (StringUtils.startsWith(contentLines[i], getCommentLinePrefix())) {
              lines.add(contentLines[i].substring(getCommentLinePrefix().length()));
            }
            else {
              break;
            }
          }
          if (!lines.isEmpty()) {
            return new FileHeaderContext().commentLines(lines);
          }
        }
      }
    }
    catch (IOException ex) {
      throw new GeneratorException("Unable parse add file header from " + file.getCanonicalPath(), ex);
    }
    return null;
  }

}
