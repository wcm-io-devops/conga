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
package io.wcm.devops.conga.generator.plugins.handlebars.helper;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.github.jknack.handlebars.Options;

import io.wcm.devops.conga.generator.spi.handlebars.HelperPlugin;

/**
 * Handlebars helper that returns the value of the first of the given property names that has a value set.
 * If none of the property names has a value null is returned.
 */
public final class ChoosePropertyHelper implements HelperPlugin<Object> {

  /**
   * Plugin/Helper name
   */
  public static final String NAME = "chooseProperty";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Object apply(Object context, Options options) throws IOException {
    Object value = getValue(context, options);
    if (value != null) {
      return value;
    }
    for (Object propertyNameExpression : options.params) {
      value = getValue(propertyNameExpression, options);
      if (value != null) {
        return value;
      }
    }
    return null;
  }

  private Object getValue(Object propertyNameExpression, Options options) {
    if (propertyNameExpression == null) {
      return null;
    }
    String propertyName = propertyNameExpression.toString();
    if (StringUtils.isBlank(propertyName)) {
      return null;
    }
    return options.get(propertyName);
  }

}
