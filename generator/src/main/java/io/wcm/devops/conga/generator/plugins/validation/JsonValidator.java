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
package io.wcm.devops.conga.generator.plugins.validation;

import io.wcm.devops.conga.generator.spi.ValidationException;
import io.wcm.devops.conga.generator.spi.ValidatorPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * Validates JSON syntax.
 */
public final class JsonValidator implements ValidatorPlugin {

  private final JsonParser jsonParser;

  /**
   * Plugin name
   */
  public static final String NAME = "json";

  private static final String FILE_EXTENSION = "json";

  /**
   * Constructor.
   */
  public JsonValidator() {
    jsonParser = new JsonParser();
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean accepts(File file) {
    return StringUtils.equalsIgnoreCase(FilenameUtils.getExtension(file.getName()), FILE_EXTENSION);
  }

  @Override
  public void validate(File file) throws ValidationException {
    try (InputStream is = new FileInputStream(file);
        Reader reader = new InputStreamReader(is, CharEncoding.UTF_8)) {
      jsonParser.parse(reader);
    }
    catch (IOException | JsonIOException | JsonSyntaxException ex) {
      throw new ValidationException("JSON file is not valid: " + ex.getMessage(), ex);
    }
  }

}
