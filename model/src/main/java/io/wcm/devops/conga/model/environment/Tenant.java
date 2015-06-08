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

import java.util.ArrayList;
import java.util.List;

/**
 * Tenant definition.
 */
public final class Tenant extends AbstractConfigurable {

  private String tenant;
  private List<String> roles = new ArrayList<>();

  public String getTenant() {
    return this.tenant;
  }

  public void setTenant(String tenant) {
    this.tenant = tenant;
  }

  public List<String> getRoles() {
    return this.roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

}
