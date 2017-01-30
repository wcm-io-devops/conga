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

import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.ValidatorContext;

/**
 * Plugin that allows to validate a generate file.
 */
public interface ValidatorPlugin extends FilePlugin<ValidatorContext, Void> {

  /**
   * Whether to apply this plugin implicitly.
   * @param file Context file
   * @param context Context objects
   * @return Implicit apply option
   */
  @Override
  default ImplicitApplyOptions implicitApply(FileContext file, ValidatorContext context) {
    return ImplicitApplyOptions.WHEN_UNCONFIGURED;
  }

  /**
   * Validates the given file
   * @param file Context file
   * @param context Context objects
   * @return nothing
   * @throws ValidationException when the validation fails.
   */
  @Override
  Void apply(FileContext file, ValidatorContext context) throws ValidationException;

}
