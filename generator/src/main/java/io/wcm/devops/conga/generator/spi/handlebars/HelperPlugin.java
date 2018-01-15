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

import java.io.IOException;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

import io.wcm.devops.conga.generator.spi.Plugin;
import io.wcm.devops.conga.generator.spi.handlebars.context.HelperContext;

/**
 * Plugin that allows to register custom Handlebars {@link Helper} extensions that can be used in all templates.
 * The name of the plugin is used as helper name for registering in Handlebars.
 * @param <T> Context type for helper
 */
public interface HelperPlugin<T> extends Plugin {

  /**
   * Apply the helper to the context.
   * @param context The context object.
   * @param options The options object.
   * @param pluginContext Plugin context
   * @return A string result.
   * @throws IOException If a template cannot be loaded.
   */
  Object apply(T context, Options options, HelperContext pluginContext) throws IOException;

}
