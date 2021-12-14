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

import static io.wcm.devops.conga.model.util.DefaultUtil.defaultEmptyList;

import java.util.ArrayList;
import java.util.List;

import io.wcm.devops.conga.model.shared.AbstractConfigurable;

/**
 * Defines a tenant for configuration files that have tenant-specific system configurations.
 * E.g. vhosts files for Apache Webserver.
 */
public final class Tenant extends AbstractConfigurable {
  private static final long serialVersionUID = 3984905428304600647L;

  private String tenant;
  private List<String> roles = new ArrayList<>();

  /**
   * Defines tenant name.
   * @return Tenant name
   */
  public String getTenant() {
    return this.tenant;
  }

  public void setTenant(String tenant) {
    this.tenant = tenant;
  }

  /**
   * Defines tenant roles. These roles are different form node roles. They can be used within the templates
   * or by multiply plugins to filter the list of tenants by their roles.
   * @return List of tenant role names
   */
  public List<String> getRoles() {
    return this.roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = defaultEmptyList(roles);
  }

}
