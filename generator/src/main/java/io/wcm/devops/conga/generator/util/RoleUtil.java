/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 wcm.io
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
package io.wcm.devops.conga.generator.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.rits.cloning.Cloner;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.model.role.Role;
import io.wcm.devops.conga.model.role.RoleFile;
import io.wcm.devops.conga.model.role.RoleInherit;
import io.wcm.devops.conga.model.role.RoleVariant;
import io.wcm.devops.conga.model.util.MapMerger;

/**
 * Helper methods for managing roles.
 */
public final class RoleUtil {

  private static final int INHERIT_MAX_LEVEL = 20;

  private RoleUtil() {
    // static methods only
  }

  /**
   * Resolve role/role variant inheritance.
   * As a result a aggregated role object is created that can be used without respecting any inheritance.
   * @param roleName Role name - role may contain inheritance relations
   * @param roles All roles for the system
   * @return Resolved role without pending inheritance relations
   * @throws GeneratorException when role or any of it's inheritance relations is not found
   */
  public static Role resolveRole(String roleName, String environmentContext, Map<String, Role> roles) throws GeneratorException {
    return resolveRole(roleName, environmentContext, roles, 0);
  }

  private static Role resolveRole(String roleName, String environmentContext, Map<String, Role> roles, int inheritLevel) throws GeneratorException {
    if (inheritLevel > INHERIT_MAX_LEVEL) {
      throw new GeneratorException("Cyclic inheritance dependency for role '" + roleName + "'.");
    }

    Role role = getRole(roleName, environmentContext, roles);

    // prefix template path with template dir path
    if (StringUtils.isNotEmpty(role.getTemplateDir())) {
      for (RoleFile file : role.getFiles()) {
        if (StringUtils.isNotEmpty(file.getTemplate())) {
          file.setTemplate(FilenameUtils.concat(role.getTemplateDir(), file.getTemplate()));
        }
      }
      role.setTemplateDir(null);
    }

    // resolve direct inherits of role
    if (!role.getInherits().isEmpty()) {
      for (RoleInherit inherit : role.getInherits()) {
        String superRoleName = inherit.getRole();
        Role superRole = resolveRole(superRoleName, environmentContext + " > " + roleName, roles, inheritLevel + 1);
        validateRole(roleName, role, superRoleName, superRole);
        mergeRole(role, superRole);
      }
      role.setInherits(null);
    }

    return role;
  }

  private static Role getRole(String roleName, String context, Map<String, Role> roles) {
    Role role = roles.get(roleName);
    if (role == null) {
      throw new GeneratorException("Role '" + roleName + "' "
          + "referenced in " + context + " does not exist.");
    }
    // clone role object because it may be changed when resolving inheritance
    return Cloner.standard().deepClone(role);
  }

  private static void validateRole(String roleName, Role role, String superRoleName, Role superRole) {
    // if the super role define variants: ensure the role inheriting defines the same variants (or more)
    if (!superRole.getVariants().isEmpty()) {
      Set<String> variants = new HashSet<>();
      for (RoleVariant variant : role.getVariants()) {
        variants.add(variant.getVariant());
      }
      for (RoleVariant superVariant : superRole.getVariants()) {
        if (!variants.contains(superVariant.getVariant())) {
          throw new GeneratorException("Role '" + roleName + "' has to define the same variants as the super role '" + superRoleName + "'.");
        }
      }
    }
  }

  private static void mergeRole(Role role, Role superRole) {
    // merge config
    role.setConfig(MapMerger.merge(role.getConfig(), superRole.getConfig()));

    // merge variant configs
    for (RoleVariant superVariant : superRole.getVariants()) {
      for (RoleVariant variant : role.getVariants()) {
        if (StringUtils.equals(superVariant.getVariant(), variant.getVariant())) {
          variant.setConfig(MapMerger.merge(variant.getConfig(), superVariant.getConfig()));
        }
      }
    }

    // merge file list
    List<RoleFile> mergedFiles = new ArrayList<>();
    mergedFiles.addAll(role.getFiles());
    mergedFiles.addAll(superRole.getFiles());
    role.setFiles(mergedFiles);
  }

}
