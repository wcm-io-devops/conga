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
package io.wcm.devops.conga.generator.spi.context;

import java.io.File;

/**
 * File context for plugins.
 */
public final class FileContext {

  private File file;
  private String charset;

  /**
   * @return File
   */
  public File getFile() {
    return file;
  }

  /**
   * @param value File
   * @return this
   */
  public FileContext file(File value) {
    file = value;
    return this;
  }

  /**
   * @return Charset
   */
  public String getCharset() {
    return charset;
  }

  /**
   * @param value Charset
   * @return this
   */
  public FileContext charset(String value) {
    charset = value;
    return this;
  }

  @Override
  public String toString() {
    return file.toString();
  }

}
