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
package io.wcm.devops.conga.generator.export;

import java.util.List;

import io.wcm.devops.conga.model.shared.AbstractConfigurable;

/**
 * Defines an export configuration for model information.
 */
public final class ModelExport extends AbstractConfigurable {
  private static final long serialVersionUID = 4642081520558431376L;

  private List<String> node;

  /**
   * Defines node export plugin names.
   * @return Node export plugin names.
   */
  public List<String> getNode() {
    return this.node;
  }

  /**
   * Defines node export plugin names.
   * @param pluginNames Node export plugin names.
   */
  public void setNode(List<String> pluginNames) {
    this.node = pluginNames;
  }

}
