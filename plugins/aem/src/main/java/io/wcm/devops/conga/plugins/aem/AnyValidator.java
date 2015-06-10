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
package io.wcm.devops.conga.plugins.aem;

import io.wcm.devops.conga.generator.spi.ValidationException;
import io.wcm.devops.conga.generator.spi.ValidatorPlugin;
import io.wcm.devops.conga.generator.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.xml.sax.InputSource;

import com.day.any.BaseHandler;
import com.day.any.ParseException;
import com.day.any.Parser;

/**
 * Validates Day ANY files.
 */
public class AnyValidator implements ValidatorPlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "any";

  private static final String FILE_EXTENSION = "any";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean accepts(File file, String charset) {
    return FileUtil.matchesExtension(file, FILE_EXTENSION);
  }

  @Override
  public void validate(File file, String charset) throws ValidationException {
    Parser parser = new Parser(new BaseHandler());
    try (InputStream is = new FileInputStream(file);
        Reader reader = new InputStreamReader(is, charset)) {
      parser.parse(new InputSource(reader));
    }
    catch (Throwable ex) {
      throw new ValidationException("ANY file is not valid: " + ex.getMessage(), ex);
    }
  }

}
