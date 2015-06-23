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

/**
 * Generic plugin interface all other plugins extend from that are called for a generated file.
 * @param <T> Context object type.
 * @param <R> Return type of the apply method.
 */
public interface FilePlugin<T, R> extends Plugin {

  /**
   * Checks if the plugin can be applied to the given file.
   * @param file Context file
   * @param context Context objects
   * @return true when the plugin can be applied to the given file.
   */
  boolean accepts(FileContext file, T context);

  /**
   * Applies the plugin operation.
   * @param file Context file
   * @param context Context objects
   * @return Return value or {@link Void}.
   */
  R apply(FileContext file, T context);

}
