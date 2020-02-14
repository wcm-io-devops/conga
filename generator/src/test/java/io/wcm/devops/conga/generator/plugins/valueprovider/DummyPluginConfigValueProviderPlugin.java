/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2020 wcm.io
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
package io.wcm.devops.conga.generator.plugins.valueprovider;

import org.apache.commons.lang3.StringUtils;

import io.wcm.devops.conga.generator.spi.ValueProviderPlugin;
import io.wcm.devops.conga.generator.spi.context.ValueProviderContext;

/**
 * Dummy plugin that returns the configured plugin configuration.
 */
public class DummyPluginConfigValueProviderPlugin implements ValueProviderPlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "dummy-plugin-config";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Object resolve(String variableName, ValueProviderContext context) {
    if (StringUtils.equals(variableName, "config")) {
      return context.getPluginContextOptions().getGenericPluginConfig().get(NAME);
    }
    else {
      return null;
    }
  }

}
