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
package io.wcm.devops.conga.generator.spi.handlebars;

import com.github.jknack.handlebars.EscapingStrategy;

import io.wcm.devops.conga.generator.spi.Plugin;

/**
 * Plugin that provides an {@link EscapingStrategy} to be used by Handlebars when replacing variables in a template.
 * Please keep in mind that this strategy is applied to every variable and does not regard the context where
 * the variable takes place in within the template.
 */
public interface EscapingStrategyPlugin extends Plugin, EscapingStrategy {

  /**
   * Checks if the plugin can be applied to files with the given file extension.
   * @param fileExtension File extension
   * @return true when the plugin can be applied to the given file.
   */
  boolean accepts(String fileExtension);

}
