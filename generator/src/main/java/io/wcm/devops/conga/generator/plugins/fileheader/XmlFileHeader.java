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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Adds file headers to XML files.
 */
public final class XmlFileHeader implements FileHeaderPlugin {

  private final DocumentBuilder documentBuilder;
  private final Transformer transformer;

  /**
   * Plugin name
   */
  public static final String NAME = "xml";

  private static final String FILE_EXTENSION = "xml";

  /**
   * Constructor.
   */
  public XmlFileHeader() {
    try {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilder = documentBuilderFactory.newDocumentBuilder();
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      transformer = transformerFactory.newTransformer();
    }
    catch (ParserConfigurationException | TransformerConfigurationException ex) {
      throw new GeneratorException("Unable to initialize validator.", ex);
    }
  }

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
      Document doc = documentBuilder.parse(file.getFile());

      // build XML comment and add it at first position
      Comment comment = doc.createComment("\n" + StringUtils.join(context.getCommentLines(), "\n") + "\n");
      doc.insertBefore(comment, doc.getChildNodes().item(0));

      // write file
      file.getFile().delete();
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(file.getFile());
      transformer.transform(source, result);
    }
    catch (SAXException | IOException | TransformerException ex) {
      throw new GeneratorException("Unable to add file hader to file: " + FileUtil.getCanonicalPath(file), ex);
    }
    return null;
  }

}
