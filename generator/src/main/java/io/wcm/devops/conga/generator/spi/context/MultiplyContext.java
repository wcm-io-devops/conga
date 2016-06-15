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
package io.wcm.devops.conga.generator.spi.context;

import java.util.Map;

import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.role.Role;
import io.wcm.devops.conga.model.role.RoleFile;

/**
 * Context for {@link io.wcm.devops.conga.generator.spi.MultiplyPlugin}.
 */
public final class MultiplyContext extends AbstractPluginContext<MultiplyContext> {

  private Role role;
  private RoleFile roleFile;
  private Environment environment;
  private Map<String, Object> config;

  /**
   * @return Role
   */
  public Role getRole() {
    return role;
  }

  /**
   * @param value Role
   * @return this
   */
  public MultiplyContext role(Role value) {
    role = value;
    return this;
  }

  /**
   * @return Role file
   */
  public RoleFile getRoleFile() {
    return roleFile;
  }

  /**
   * @param value Role file
   * @return this
   */
  public MultiplyContext roleFile(RoleFile value) {
    roleFile = value;
    return this;
  }

  /**
   * @return Environment
   */
  public Environment getEnvironment() {
    return environment;
  }

  /**
   * @param value Environment
   * @return this
   */
  public MultiplyContext environment(Environment value) {
    environment = value;
    return this;
  }

  /**
   * @return Config
   */
  public Map<String, Object> getConfig() {
    return config;
  }

  /**
   * @param value Config
   * @return this
   */
  public MultiplyContext config(Map<String, Object> value) {
    config = value;
    return this;
  }

}
