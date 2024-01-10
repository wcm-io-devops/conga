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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

import io.wcm.devops.conga.resource.Resource;

/**
 * Shared functionality for model readers.
 */
public abstract class AbstractModelReader<T> implements ModelReader<T> {

  private static final String YAML_EXTENSION = "yaml";
  private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(YAML_EXTENSION);

  private final Yaml yaml;

  /**
   * @param yaml YAML
   */
  public AbstractModelReader(Yaml yaml) {
    this.yaml = yaml;
  }

  @Override
  public boolean accepts(Resource file) {
    return SUPPORTED_EXTENSIONS.contains(file.getFileExtension().toLowerCase());
  }

  @Override
  public final T read(Resource file) throws IOException {
    try (InputStream is = file.getInputStream()) {
      return read(is);
    }
  }

  @Override
  public final T read(InputStream is) throws IOException {
    return read(new InputStreamReader(is, StandardCharsets.UTF_8));
  }

  @Override
  @SuppressWarnings("unchecked")
  public final T read(Reader reader) {
    return (T)yaml.load(reader);
  }

}
