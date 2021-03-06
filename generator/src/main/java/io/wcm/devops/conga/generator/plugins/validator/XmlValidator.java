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

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.ValidationException;
import io.wcm.devops.conga.generator.spi.ValidatorPlugin;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.ValidatorContext;
import io.wcm.devops.conga.generator.util.FileUtil;

/**
 * Validates XML syntax.
 */
public final class XmlValidator implements ValidatorPlugin {

  private final DocumentBuilder documentBuilder;

  /**
   * Plugin name
   */
  public static final String NAME = "xml";

  private static final String FILE_EXTENSION = "xml";

  /**
   * Constructor.
   */
  public XmlValidator() {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      documentBuilder = factory.newDocumentBuilder();
    }
    catch (ParserConfigurationException ex) {
      throw new GeneratorException("Unable to initialize validator.", ex);
    }
  }

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
    try {
      documentBuilder.parse(file.getFile());
    }
    catch (SAXException | IOException ex) {
      throw new ValidationException("XML file is not valid: " + ex.getMessage(), ex);
    }
    return null;
  }

}
