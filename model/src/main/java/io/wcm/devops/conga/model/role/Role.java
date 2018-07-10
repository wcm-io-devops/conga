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

import static io.wcm.devops.conga.model.util.DefaultUtil.defaultEmptyList;

import java.util.ArrayList;
import java.util.List;

import io.wcm.devops.conga.model.shared.AbstractConfigurable;

/**
 * Defines a role with a set of configuration files to generate.
 * The filename of the role YAML file is the role name, it's not included in the model.
 */
public final class Role extends AbstractConfigurable {

  private List<RoleInherit> inherits = new ArrayList<>();
  private List<RoleVariant> variants = new ArrayList<>();
  private String templateDir;
  private List<RoleFile> files = new ArrayList<>();
  private List<String> sensitiveConfigParameters = new ArrayList<>();

  /**
   * Defines inheritance relations for this role.
   * @return List of inheritance relations
   */
  public List<RoleInherit> getInherits() {
    return this.inherits;
  }

  public void setInherits(List<RoleInherit> inherits) {
    this.inherits = defaultEmptyList(inherits);
  }

  /**
   * Defines the role variants available for this role. Role variants can be used to apply a slightly different
   * set of configuration templates or values to a node based on a variant.
   * @return List of role variant definitions
   */
  public List<RoleVariant> getVariants() {
    return this.variants;
  }

  public void setVariants(List<RoleVariant> variants) {
    this.variants = defaultEmptyList(variants);
  }

  /**
   * Defines the directory where the role template files are located. The path is relative to the tempaltes root
   * directory. If missing the files are searched within the templates root directory.
   * @return Relative path to template files
   */
  public String getTemplateDir() {
    return this.templateDir;
  }

  public void setTemplateDir(String templateDirectory) {
    this.templateDir = templateDirectory;
  }

  /**
   * Defines the files that should be generated for this role.
   * @return List of file definitions.
   */
  public List<RoleFile> getFiles() {
    return this.files;
  }

  public void setFiles(List<RoleFile> files) {
    this.files = defaultEmptyList(files);
  }

  /**
   * List of configuration parameter names that contain sensitive data (like passwords)
   * that should be encrypted on serialization e.g. in model export files.
   * @return List of configuration parameter names (with "." as hierarchy separator)
   */
  public List<String> getSensitiveConfigParameters() {
    return defaultEmptyList(this.sensitiveConfigParameters);
  }

  public void setSensitiveConfigParameters(List<String> sensitiveProperties) {
    this.sensitiveConfigParameters = sensitiveProperties;
  }

}
