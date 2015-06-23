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

import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.role.Role;
import io.wcm.devops.conga.model.role.RoleFile;

import java.util.List;
import java.util.Map;

/**
 * Context object for {@link io.wcm.devops.conga.generator.spi.MultiplyPlugin} calls.
 */
public interface MultiplyContext extends PluginContext {
  
  /**
   * Current role.
   * @return Role
   */
  Role getRole();
  
  /**
   * Current generated file of role
   * @return Role file
   */
  RoleFile getRoleFile();
  
  /**
   * Environment
   * @return Environment
   */
  Environment getEnvironment();
  
  /**
   * Merged configuration for current node/role.
   * @return Configuration
   */
  Map<String, Object> getConfig();

}
