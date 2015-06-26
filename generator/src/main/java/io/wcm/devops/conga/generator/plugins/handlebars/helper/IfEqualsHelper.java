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
package io.wcm.devops.conga.generator.plugins.handlebars.helper;

import io.wcm.devops.conga.generator.spi.handlebars.HelperPlugin;

import java.io.IOException;
import java.util.Objects;

import com.github.jknack.handlebars.Options;

/**
 * Handlebars helper that evaluates two objects for equality and shows/hides the contained block accordingly.
 */
public class IfEqualsHelper implements HelperPlugin<Object> {

  /**
   * Plugin/Helper name
   */
  public static final String NAME = "ifEquals";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public CharSequence apply(Object context, Options options) throws IOException {
    if (!options.isFalsy(context)
        && options.params.length > 0
        && Objects.equals(context, options.param(0))) {
      return options.fn();
    }
    else {
      return options.inverse();
    }
  }

}
