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

/**
 * Handlebars helper that extends the each helper by iterating only on list items that have a specified property set.
 * "Deep map access" with dot notation is supported as property names as well.
 */
public final class EachIfHelper extends AbstractEachIfHelper {

  /**
   * Plugin/Helper name
   */
  public static final String NAME = "eachIf";

  /**
   * Constructor
   */
  public EachIfHelper() {
    super((value, options) -> value != null);
  }

  @Override
  public String getName() {
    return NAME;
  }

}
