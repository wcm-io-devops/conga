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
package io.wcm.devops.conga.generator.spi;

import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.role.Role;
import io.wcm.devops.conga.model.role.RoleFile;

import java.util.List;
import java.util.Map;

/**
 * Plugin that allows to generate multiple files from one template.
 */
public interface MultiplyPlugin extends Plugin {

  /**
   * Execute multiply operation.
   * @param role Role
   * @param roleFile Role file
   * @param environment Environment
   * @param config Configuration
   * @return List of configurations with adapted parameters per multiply item. Parameters from this map can be used in
   *         placeholder for directory or filename to generate multiple files.
   */
  List<Map<String, Object>> multiply(Role role, RoleFile roleFile, Environment environment, Map<String, Object> config);

}
