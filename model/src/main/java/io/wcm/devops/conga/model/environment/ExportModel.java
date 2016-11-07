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
package io.wcm.devops.conga.model.environment;

import io.wcm.devops.conga.model.shared.AbstractConfigurable;

/**
 * Defines an export configuration for model information.
 */
public final class ExportModel extends AbstractConfigurable {

  private String node;

  /**
   * Defines node export plugin name.
   * @return Node export plugin name.
   */
  public String getNode() {
    return this.node;
  }

  public void setNode(String node) {
    this.node = node;
  }

}
