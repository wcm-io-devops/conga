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
package io.wcm.devops.conga.generator.spi.context;

import java.io.File;
import java.util.Map;

/**
 * Context object for {@link io.wcm.devops.conga.generator.spi.ValidatorPlugin} calls.
 */
public interface ValidatorContext extends PluginContext {

  /**
   * File that was generated
   * @return File
   */
  File getFile();

  /**
   * Charset for file
   * @return Charset
   */
  String getCharset();

  /**
   * Validator options from role definition.
   * @return Options configuration
   */
  Map<String, Object> getOptions();

}
