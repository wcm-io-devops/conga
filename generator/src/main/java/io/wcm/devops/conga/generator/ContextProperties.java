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
   * Environment version
   */
  public static final String VERSION = "version";

  /**
   * Current node role name
   */
  public static final String ROLE = "nodeRole";

  /**
   * Current node role variant name. This is only set, if the node role has only one variant assigned.
   */
  public static final String ROLE_VARIANT = "nodeRoleVariant";

  /**
   * List of current node role variant names. Empty if no variant is assigned. Single element if exactly one variant is
   * assigned. List if multiples are assigned.
   */
  public static final String ROLE_VARIANTS = "nodeRoleVariants";

  /**
   * Environment name
   */
  public static final String ENVIRONMENT = "environment";

  /**
   * Current node name
   */
  public static final String NODE = "node";

  /**
   * List of all nodes.
   * Each node has properties as defined in {@link io.wcm.devops.conga.model.environment.Node}.
   */
  public static final String NODES = "nodes";

  /**
   * Map with node roles with each entry containing a list of all nodes with this role.
   * Each node has properties as defined in {@link io.wcm.devops.conga.model.environment.Node}.
   */
  public static final String NODES_BY_ROLE = "nodesByRole";

  /**
   * Map with node roles with each entry containing a map with node role variants each entry containing a list of all
   * nodes with this role and variant.
   * Each node has properties as defined in {@link io.wcm.devops.conga.model.environment.Node}.
   */
  public static final String NODES_BY_ROLE_VARIANT = "nodesByRoleVariant";

  /**
   * List of all tenants.
   * Each tenant has properties as defined in {@link io.wcm.devops.conga.model.environment.Tenant}.
   */
  public static final String TENANTS = "tenants";

  /**
   * Map with tenant roles with each entry containing a list of all tenants with this role.
   * Each tenant has properties as defined in {@link io.wcm.devops.conga.model.environment.Tenant}.
   */
  public static final String TENANTS_BY_ROLE = "tenantsByRole";

  /**
   * Current tenant name.
   * This is only set if the {@link io.wcm.devops.conga.generator.plugins.multiply.TenantMultiply} plugin is used.
   */
  public static final String TENANT = "tenant";

  /**
   * List of current tenant's role names
   * This is only set if the {@link io.wcm.devops.conga.generator.plugins.multiply.TenantMultiply} plugin is used.
   */
  public static final String TENANT_ROLES = "tenantRoles";

}
