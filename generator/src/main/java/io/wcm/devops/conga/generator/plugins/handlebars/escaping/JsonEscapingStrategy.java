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
package io.wcm.devops.conga.generator.plugins.handlebars.escaping;

import java.util.Map;

import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.EntityArrays;
import org.apache.commons.text.translate.LookupTranslator;

import io.wcm.devops.conga.generator.spi.handlebars.EscapingStrategyPlugin;
import io.wcm.devops.conga.generator.spi.handlebars.context.EscapingStrategyContext;
import io.wcm.devops.conga.generator.util.FileUtil;

/**
 * Escapes for JSON files.
 */
public class JsonEscapingStrategy implements EscapingStrategyPlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "json";

  private static final String FILE_EXTENSION = "json";

  /**
   * Copy from {@link org.apache.commons.text.StringEscapeUtils#escapeJson(String)},
   * but without explicitly escaping unicode chars. The escaping of forward slashes is removed as well also part of the
   * JSON specifiction. Both for better readability.
   */
  private static final CharSequenceTranslator ESCAPE_JSON =
      new AggregateTranslator(
          new LookupTranslator(
              Map.of(
                  "\"", "\\\"",
                  "\\", "\\\\"
              )),
            new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE)
          );

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean accepts(String fileExtension, EscapingStrategyContext pluginContext) {
    return FileUtil.matchesExtension(fileExtension, FILE_EXTENSION);
  }

  @Override
  public String escape(CharSequence value, EscapingStrategyContext pluginContext) {
    return value == null ? null : ESCAPE_JSON.translate(value);
  }

}
