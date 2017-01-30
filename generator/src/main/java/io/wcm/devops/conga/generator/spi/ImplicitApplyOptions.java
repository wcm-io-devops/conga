/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 wcm.io
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

/**
 * Whether to apply a plugin implicitly.
 */
public enum ImplicitApplyOptions {

  /**
   * Plugin is never applied implicitly.
   */
  NEVER,

  /**
   * Plugin is applied implicitly when no other plugin of the same type is configured explicitely for the given
   * context.
   */
  WHEN_UNCONFIGURED,

  /**
   * Plugin is always applied to all accepted contexts.
   */
  ALWAYS

}
