/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2016 wcm.io
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
package io.wcm.devops.conga.generator.spi.export.context;

import java.util.List;
import java.util.Map;

/**
 * Export role data for node.
 */
public final class ExportNodeRoleData {

  private String role;
  private List<String> roleVariants;
  private List<GeneratedFileContext> files;
  private Map<String, Object> config;
  private List<ExportNodeRoleTenantData> tenantData;

  /**
   * @return Role name
   */
  public String getRole() {
    return this.role;
  }

  /**
   * @param value Role name
   * @return this
   */
  public ExportNodeRoleData role(String value) {
    this.role = value;
    return this;
  }

  /**
   * @return Role variant names
   */
  public List<String> getRoleVariants() {
    return this.roleVariants;
  }

  /**
   * @param values Role variant names
   * @return this
   */
  public ExportNodeRoleData roleVariant(List<String> values) {
    this.roleVariants = values;
    return this;
  }

  /**
   * @return Generated files
   */
  public List<GeneratedFileContext> getFiles() {
    return this.files;
  }

  /**
   * @param value Generated files
   * @return this
   */
  public ExportNodeRoleData files(List<GeneratedFileContext> value) {
    this.files = value;
    return this;
  }

  /**
   * @return Configuration
   */
  public Map<String, Object> getConfig() {
    return this.config;
  }

  /**
   * @param value Configuration
   * @return this
   */
  public ExportNodeRoleData config(Map<String, Object> value) {
    this.config = value;
    return this;
  }

  /**
   * @return Tenant data
   */
  public List<ExportNodeRoleTenantData> getTenantData() {
    return this.tenantData;
  }

  /**
   * @param value Tenant data
   * @return this
   */
  public ExportNodeRoleData tenantData(List<ExportNodeRoleTenantData> value) {
    this.tenantData = value;
    return this;
  }

}
