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
package io.wcm.devops.conga.generator.plugins.multiply;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import io.wcm.devops.conga.generator.spi.MultiplyPlugin;
import io.wcm.devops.conga.generator.spi.context.MultiplyContext;

/**
 * Multiplier that does not multiply but generate the file once.
 */
public final class NoneMultiply implements MultiplyPlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "none";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public List<Map<String, Object>> multiply(MultiplyContext context) {
    return ImmutableList.of(context.getConfig());
  }

}
