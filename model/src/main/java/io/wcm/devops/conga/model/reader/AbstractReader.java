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
package io.wcm.devops.conga.model.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.lang3.CharEncoding;
import org.yaml.snakeyaml.Yaml;

/**
 * Shared functionality for readers.
 */
public abstract class AbstractReader<T> {

  private final Yaml yaml;

  /**
   * @param yaml YAML
   */
  public AbstractReader(Yaml yaml) {
    this.yaml = yaml;
  }

  /**
   * Read model
   * @param file Model file
   * @return Model object
   * @throws IOException
   */
  public final T read(File file) throws IOException {
    try (InputStream is = new FileInputStream(file)) {
      return read(is);
    }
  }

  /**
   * Read model
   * @param is Model file
   * @return Model object
   * @throws IOException
   */
  public final T read(InputStream is) throws IOException {
    return read(new InputStreamReader(is, CharEncoding.UTF_8));
  }

  /**
   * Read model
   * @param reader Model file
   * @return Model object
   */
  @SuppressWarnings("unchecked")
  public final T read(Reader reader) {
    return (T)yaml.load(reader);
  }

}
