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
 * Role assignment for a node in an environment.
 */
public final class NodeRole extends AbstractConfigurable {

  private String role;
  private String variant;

  /**
   * Defines the role name.
   * @return Role name
   */
  public String getRole() {
    return this.role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  /**
   * Defines the role variant name. Only ony variant can be chosen per role. The role has to define the variant.
   * @return Role variant name
   */
  public String getVariant() {
    return this.variant;
  }

  public void setVariant(String variant) {
    this.variant = variant;
  }

}
