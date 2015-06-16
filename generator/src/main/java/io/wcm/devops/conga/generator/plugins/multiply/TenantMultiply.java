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
package io.wcm.devops.conga.generator.plugins.multiply;

import io.wcm.devops.conga.generator.ContextProperties;
import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.MultiplyContext;
import io.wcm.devops.conga.generator.spi.MultiplyPlugin;
import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.environment.Tenant;
import io.wcm.devops.conga.model.role.Role;
import io.wcm.devops.conga.model.role.RoleFile;
import io.wcm.devops.conga.model.util.MapMerger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableMap;

/**
 * Multiplies a file for each tenant with a matching tenant role.
 */
public final class TenantMultiply implements MultiplyPlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "tenant";

  static final String ROLES_PROPERTY = "roles";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public List<MultiplyContext> multiply(Role role, RoleFile roleFile, Environment environment,
      Map<String, Object> config, Map<String, Object> contextVariables) {
    List<MultiplyContext> contexts = new ArrayList<>();

    for (Tenant tenant : environment.getTenants()) {
      if (StringUtils.isEmpty(tenant.getTenant())) {
        throw new GeneratorException("Tenant without tenant name detected.");
      }
      if (acceptTenant(tenant, roleFile.getMultiplyOptions())) {
        Map<String, Object> mergedConfig = MapMerger.merge(tenant.getConfig(), config);

        Map<String, Object> mergedContextVariables = MapMerger.merge(ImmutableMap.<String, Object>builder()
            .put(ContextProperties.TENANT, tenant.getTenant())
            .put(ContextProperties.TENANT_ROLES, tenant.getRoles())
            .build(), contextVariables);

        contexts.add(new MultiplyContext(mergedConfig, mergedContextVariables));
      }
    }

    return contexts;
  }

  /**
   * Ensures that required roles match for the given tenant.
   * @param tenant Tenant
   * @param multiplyOptions Options with required roles.
   * @return true if matches
   */
  @SuppressWarnings("unchecked")
  private boolean acceptTenant(Tenant tenant, Map<String, Object> multiplyOptions) {
    List<String> roles = (List<String>)multiplyOptions.get(ROLES_PROPERTY);
    if (roles != null && !roles.isEmpty()) {
      for (String role : roles) {
        if (tenant.getRoles().contains(role)) {
          return true;
        }
      }
      return false;
    }
    else {
      return true;
    }
  }

}
