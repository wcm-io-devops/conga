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
package io.wcm.devops.conga.generator;

/**
 * Default configuration property names implicitly set by the configuration generator.
 * The keys can also be used as placeholders with syntax ${key} in filename patterns.
 * These names should not be used inside custom configuration sections of roles or environments.
 */
public final class ContextProperties {

  private ContextProperties() {
    // constants only
  }

  /**
   * Node role name
   */
  public static final String ROLE = "nodeRole";

  /**
   * Role variant name
   */
  public static final String ROLE_VARIANT = "nodeRoleVariant";

  /**
   * Environment name
   */
  public static final String ENVIRONMENT = "environment";

  /**
   * Node name
   */
  public static final String NODE = "node";

  /**
   * Tenant name.
   * This is only set if the {@link io.wcm.devops.conga.generator.plugins.multiply.TenantMultiply} plugin is used.
   */
  public static final String TENANT = "tenant";

  /**
   * List of tenant role names
   * This is only set if the {@link io.wcm.devops.conga.generator.plugins.multiply.TenantMultiply} plugin is used.
   */
  public static final String TENANT_ROLES = "tenantRoles";

}
