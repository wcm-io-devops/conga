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

import org.apache.commons.lang3.StringUtils;

import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.FileHeaderContext;
import io.wcm.devops.conga.generator.util.FileUtil;

/**
 * Adds file headers to Unix script files (esp. Bash).
 */
public final class UnixShellScriptFileHeader extends AbstractFileHeader {

  /**
   * Plugin name
   */
  public static final String NAME = "unixShellScript";

  private static final String FILE_EXTENSION = "sh";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean accepts(FileContext file, FileHeaderContext context) {
    return FileUtil.matchesExtension(file, FILE_EXTENSION);
  }

  @Override
  protected String getCommentLinePrefix() {
    return "# ";
  }

  @Override
  protected String getBlockSuffix() {
    return getLineBreak();
  }

  @Override
  protected int getInsertPosition(String content) {
    // keep shebang on first line if present
    if (StringUtils.startsWith(content, "#!")) {
      return StringUtils.indexOf(content, "\n") + 1;
    }
    return 0;
  }

  @Override
  public FileHeaderContext extract(FileContext file) {
    return extractFileHeaderWithLinePrefixes(file);
  }

}
