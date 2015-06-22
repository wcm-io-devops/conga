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

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;

/**
 * Plugin that allows to post-process a generated file.
 */
public interface PostProcessorPlugin extends Plugin {

  /**
   * @param file File
   * @param charset File charset
   * @return true when post process can be applied to the given context.
   */
  boolean accepts(File file, String charset);

  /**
   * Execute post process operation.
   * @param charset File charset
   * @param file File
   * @param options Post processor options
   * @param logger Logger
   */
  void postProcess(File file, String charset, Map<String, Object> options, Logger logger);

}
