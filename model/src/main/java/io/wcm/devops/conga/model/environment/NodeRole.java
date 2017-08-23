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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.wcm.devops.conga.model.shared.AbstractConfigurable;

/**
 * Role assignment for a node in an environment.
 */
public final class NodeRole extends AbstractConfigurable {

  private String role;
  private String variant;
  private List<String> variants;

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
   * Defines the role variant name. The role has to define the variant.
   * With this parameter only one variant can be chosen per role.
   * @return Role variant name
   */
  public String getVariant() {
    return this.variant;
  }

  public void setVariant(String variant) {
    this.variant = variant;
  }

  /**
   * Defines multiple role variant names. The role has to define the variants.
   * If this is set any value set to the 'variant' parameter is ignored.
   * @return Role variant names
   */
  public List<String> getVariants() {
    return this.variants;
  }

  public void setVariants(List<String> variants) {
    this.variants = variants;
  }

  /**
   * Get list of defined variants, regardless if the 'variant' or 'variants' parameter was used.
   * @return List of defined variants
   */
  public List<String> getAggregatedVariants() {
    List<String> result = new ArrayList<>();
    if (this.variants != null) {
      result.addAll(this.variants);
    }
    else if (StringUtils.isNotBlank(this.variant)) {
      result.add(this.variant);
    }
    return result;
  }

}
