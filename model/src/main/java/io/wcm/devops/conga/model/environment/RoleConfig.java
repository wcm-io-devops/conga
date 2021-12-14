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
 * Global role-specific configuration. Configuration defined here applies to all nodes that have this role assigned.
 */
public final class RoleConfig extends AbstractConfigurable {
  private static final long serialVersionUID = -8636577741823687304L;

  private String role;

  /**
   * Defines role name.
   * @return Role name
   */
  public String getRole() {
    return this.role;
  }

  public void setRole(String role) {
    this.role = role;
  }

}
