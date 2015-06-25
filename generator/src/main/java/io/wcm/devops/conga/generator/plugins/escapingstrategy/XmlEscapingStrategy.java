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
package io.wcm.devops.conga.generator.plugins.escapingstrategy;

import io.wcm.devops.conga.generator.spi.EscapingStrategyPlugin;
import io.wcm.devops.conga.generator.spi.context.EscapingStrategyContext;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.util.FileUtil;

import org.apache.commons.lang3.StringEscapeUtils;

import com.github.jknack.handlebars.EscapingStrategy;

/**
 * Escapes for XML files.
 */
public class XmlEscapingStrategy implements EscapingStrategyPlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "xml";

  private static final String FILE_EXTENSION = "xml";

  private static final EscapingStrategy ESCAPING_STRATEGY = new EscapingStrategy() {
    @Override
    public String escape(CharSequence value) {
      return value == null ? null : StringEscapeUtils.escapeXml10(value.toString());
    }
  };

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean accepts(FileContext file, EscapingStrategyContext context) {
    return FileUtil.matchesExtension(file, FILE_EXTENSION);
  }

  @Override
  public EscapingStrategy apply(FileContext file, EscapingStrategyContext context) {
    return ESCAPING_STRATEGY;
  }

}
