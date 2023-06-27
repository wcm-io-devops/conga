/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2023 wcm.io
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
package io.wcm.devops.conga.generator.plugins.handlebars.helper;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.github.jknack.handlebars.Options;

import io.wcm.devops.conga.generator.spi.handlebars.HelperPlugin;
import io.wcm.devops.conga.generator.spi.handlebars.context.HelperContext;

/**
 * Handlebars helper that ensures a given property is not set/present.
 * If it is, an exception is thrown with the property name.
 * Optionally, a second parameter can be provided with a custom error message.
 */
public final class DisallowPropertyHelper implements HelperPlugin<Object> {

  /**
   * Plugin/Helper name
   */
  public static final String NAME = "disallowProperty";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Object apply(Object context, Options options, HelperContext pluginContext) throws IOException {
    if (isPropertyPresent(context, options)) {
      String errorMessage = "Disallowed property is set: " + context;
      if (options.params.length > 0 && options.params[0] != null) {
        errorMessage = options.params[0].toString();
      }
      throw new IOException(errorMessage);
    }
    return null;
  }

  private boolean isPropertyPresent(Object propertyNameExpression, Options options) {
    if (propertyNameExpression == null) {
      return false;
    }
    String propertyName = propertyNameExpression.toString();
    if (StringUtils.isBlank(propertyName)) {
      return false;
    }
    return options.get(propertyName) != null;
  }

}
