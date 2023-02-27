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
package io.wcm.devops.conga.model.reader;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import io.wcm.devops.conga.model.role.Role;

/**
 * Reads role definitions.
 */
public final class RoleReader extends AbstractModelReader<Role> {

  /**
   * Constructor
   */
  public RoleReader() {
    super(getYaml());
  }

  private static Yaml getYaml() {
    Constructor constructor = new Constructor(Role.class, new LoaderOptions());
    return new Yaml(constructor);
  }

}
