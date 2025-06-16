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
package io.wcm.devops.conga.model.util;

import org.yaml.snakeyaml.LoaderOptions;

/**
 * Helper methods for SnakeYAML.
 */
public final class YamlUtil {

  /*
   * Increase default codepoint limit from 3MB to 256MB.
   */
  private static final int YAML_CODEPOINT_LIMIT = 256 * 1024 * 1024;

  private YamlUtil() {
    // static methods only
  }

  /**
   * Create a new loader options instances with default configuration.
   * @return SnakeYAML loader option.s
   */
  public static LoaderOptions createLoaderOptions() {
    LoaderOptions options = new LoaderOptions();
    options.setCodePointLimit(YAML_CODEPOINT_LIMIT);
    return options;
  }

}
