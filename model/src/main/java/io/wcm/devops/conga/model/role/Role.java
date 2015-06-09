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

import java.util.ArrayList;
import java.util.List;

/**
 * Role definition.
 */
public final class Role extends AbstractConfigurable {

  private List<String> variants = new ArrayList<>();
  private String templateDir;
  private List<RoleFile> files = new ArrayList<>();

  public List<String> getVariants() {
    return this.variants;
  }

  public void setVariants(List<String> variants) {
    this.variants = variants;
  }

  public String getTemplateDir() {
    return this.templateDir;
  }

  public void setTemplateDir(String templateDirectory) {
    this.templateDir = templateDirectory;
  }

  public List<RoleFile> getFiles() {
    return this.files;
  }

  public void setFiles(List<RoleFile> files) {
    this.files = files;
  }

}