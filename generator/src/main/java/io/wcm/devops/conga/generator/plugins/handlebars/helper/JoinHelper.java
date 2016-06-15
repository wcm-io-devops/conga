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

import java.io.IOException;

import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.helper.StringHelpers;

import io.wcm.devops.conga.generator.spi.handlebars.HelperPlugin;

/**
 * Handlebars helper that joins a list to a single string.
 * Uses {@link StringHelpers#join}.
 */
public final class JoinHelper implements HelperPlugin<Object> {

  /**
   * Plugin/Helper name
   */
  public static final String NAME = "join";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public CharSequence apply(Object context, Options options) throws IOException {
    return StringHelpers.join.apply(context, options);
  }

}
