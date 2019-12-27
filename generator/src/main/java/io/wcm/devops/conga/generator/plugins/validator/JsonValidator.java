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
package io.wcm.devops.conga.generator.plugins.validator;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import io.wcm.devops.conga.generator.spi.ValidationException;
import io.wcm.devops.conga.generator.spi.ValidatorPlugin;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.ValidatorContext;
import io.wcm.devops.conga.generator.util.FileUtil;

/**
 * Validates JSON syntax.
 */
public final class JsonValidator implements ValidatorPlugin {

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
  public boolean accepts(FileContext file, ValidatorContext context) {
    return FileUtil.matchesExtension(file, FILE_EXTENSION);
  }

  @Override
  public Void apply(FileContext file, ValidatorContext context) throws ValidationException {
    try (InputStream is = new BufferedInputStream(new FileInputStream(file.getFile()));
        Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
      JsonParser.parseReader(reader);
    }
    catch (IOException | JsonIOException | JsonSyntaxException ex) {
      throw new ValidationException("JSON file is not valid: " + ex.getMessage(), ex);
    }
    return null;
  }

}
