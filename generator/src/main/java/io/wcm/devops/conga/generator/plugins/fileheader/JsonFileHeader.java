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

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.FileHeaderPlugin;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.FileHeaderContext;
import io.wcm.devops.conga.generator.util.FileUtil;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

/**
 * Adds JSON file header.
 */
public final class JsonFileHeader implements FileHeaderPlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "json";

  private static final String FILE_EXTENSION = "json";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean accepts(FileContext file, FileHeaderContext context) {
    return FileUtil.matchesExtension(file, FILE_EXTENSION);
  }

  @Override
  public Void apply(FileContext file, FileHeaderContext context) {
    try {
      String content = FileUtils.readFileToString(file.getFile(), CharEncoding.UTF_8);

      content = "/*\n"
          + StringUtils.join(context.getCommentLines(), "\n")
          + "*/\n"
          + "\n"
          + content;

      file.getFile().delete();
      FileUtils.write(file.getFile(), content, CharEncoding.UTF_8);
    }
    catch (IOException ex) {
      throw new GeneratorException("Unable to add file header to " + FileUtil.getCanonicalPath(file), ex);
    }
    return null;
  }

}
