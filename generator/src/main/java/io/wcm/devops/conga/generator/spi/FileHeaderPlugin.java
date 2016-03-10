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
package io.wcm.devops.conga.generator.spi;

import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.FileHeaderContext;

/**
 * Plugin that generates a file header comment with info that the file was automatically generated
 * and further info like a timestamp.
 */
public interface FileHeaderPlugin extends FilePlugin<FileHeaderContext, Void> {

  /**
   * Applies the comment file header.
   * @param file Context file
   * @param context Context objects
   * @return nothing
   */
  @Override
  Void apply(FileContext file, FileHeaderContext context);

  /**
   * Extract comment lines from file header.
   * @param file Context file
   * @return File header context with comment lines. Returns null if extraction not possible or no comments present.
   */
  default FileHeaderContext extract(FileContext file) {
    return null;
  }

}
