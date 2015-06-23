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

import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.FileHeaderContext;
import io.wcm.devops.conga.generator.util.FileUtil;

/**
 * Adds file headers to Windows .bat or .cmd files.
 */
public final class WindowsCmdScriptFileHeader extends AbstractFileHeader {

  /**
   * Plugin name
   */
  public static final String NAME = "windowsCmdScript";

  private static final String FILE_EXTENSION_BAT = "bat";
  private static final String FILE_EXTENSION_CMD = "cmd";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean accepts(FileContext file, FileHeaderContext context) {
    return FileUtil.matchesExtension(file, FILE_EXTENSION_BAT)
        || FileUtil.matchesExtension(file, FILE_EXTENSION_CMD);
  }

  @Override
  protected String getLineBreak() {
    return "\r\n";
  }

  @Override
  protected String getCommentLinePrefix() {
    return "REM ";
  }

  @Override
  protected String getBlockSuffix() {
    return getLineBreak();
  }

}
