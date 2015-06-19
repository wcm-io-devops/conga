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

import io.wcm.devops.conga.resource.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Reads a model.
 * @param <T> Model type.
 */
public interface ModelReader<T> {

  /**
   * Checks if the given file is accepted by this reader.
   * @param file File
   * @return true if accepted
   */
  boolean accepts(Resource file);

  /**
   * Read model
   * @param file Model file
   * @return Model object
   * @throws IOException
   */
  T read(Resource file) throws IOException;

  /**
   * Read model
   * @param is Model file
   * @return Model object
   * @throws IOException
   */
  T read(InputStream is) throws IOException;

  /**
   * Read model
   * @param reader Model file
   * @return Model object
   */
  T read(Reader reader);

}
