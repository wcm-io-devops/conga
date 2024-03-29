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
package io.wcm.devops.conga.model.role;

import io.wcm.devops.conga.model.shared.AbstractConfigurable;

/**
 * Defines a named variant of a role.
 */
public final class RoleVariant extends AbstractConfigurable {
  private static final long serialVersionUID = 8927953093805130622L;

  private String variant;

  /**
   * Defines the variant name.
   * @return Variant name
   */
  public String getVariant() {
    return this.variant;
  }

  public void setVariant(String variant) {
    this.variant = variant;
  }

}
