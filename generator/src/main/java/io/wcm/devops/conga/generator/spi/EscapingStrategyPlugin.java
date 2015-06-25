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
package io.wcm.devops.conga.generator.spi;

import io.wcm.devops.conga.generator.spi.context.EscapingStrategyContext;
import io.wcm.devops.conga.generator.spi.context.FileContext;

import com.github.jknack.handlebars.EscapingStrategy;

/**
 * Plugin that provides an {@link EscapingStrategy} to be used by Handlebars when replacing variables in a template.
 * Please keep in mind that this strategy is applied to every variable and does not regard the context where
 * the variable takes place in within the template.
 */
public interface EscapingStrategyPlugin extends FilePlugin<EscapingStrategyContext, EscapingStrategy> {

  /**
   * Provides an escaping strategy for the given file.
   * @param file Context file
   * @param context Context objects
   * @return Escaping strategy
   */
  @Override
  EscapingStrategy apply(FileContext file, EscapingStrategyContext context);

}
