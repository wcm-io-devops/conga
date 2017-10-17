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
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.github.jknack.handlebars.Options;

import io.wcm.devops.conga.generator.spi.handlebars.HelperPlugin;

/**
 * Handlebars helper that checks if all given property names have a value set.
 * If not, an exception is thrown containing the missing property names.
 */
public final class EnsurePropertiesHelper implements HelperPlugin<Object> {

  /**
   * Plugin/Helper name
   */
  public static final String NAME = "ensureProperties";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Object apply(Object context, Options options) throws IOException {
    Set<String> missingPropertyNames = new TreeSet<>();
    checkProperty(context, options, missingPropertyNames);
    for (Object propertyNameExpression : options.params) {
      checkProperty(propertyNameExpression, options, missingPropertyNames);
    }
    if (!missingPropertyNames.isEmpty()) {
      if (missingPropertyNames.size() == 1) {
        throw new IOException("Mandatory property not set: " + missingPropertyNames.iterator().next());
      }
      else {
        throw new IOException("Mandatory properties not set: " + StringUtils.join(missingPropertyNames, ", "));
      }
    }
    return null;
  }

  private void checkProperty(Object propertyNameExpression, Options options, Set<String> missingPropertyNames) {
    if (propertyNameExpression == null) {
      return;
    }
    String propertyName = propertyNameExpression.toString();
    if (StringUtils.isBlank(propertyName)) {
      return;
    }
    Object value = options.get(propertyName);
    if (value == null) {
      missingPropertyNames.add(propertyName);
    }
  }

}
