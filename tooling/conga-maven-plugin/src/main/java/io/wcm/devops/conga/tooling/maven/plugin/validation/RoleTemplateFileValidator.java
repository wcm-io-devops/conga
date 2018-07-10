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
package io.wcm.devops.conga.tooling.maven.plugin.validation;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoFailureException;

import com.github.jknack.handlebars.Handlebars;

import io.wcm.devops.conga.generator.handlebars.HandlebarsManager;
import io.wcm.devops.conga.generator.plugins.handlebars.escaping.NoneEscapingStrategy;
import io.wcm.devops.conga.generator.util.FileUtil;
import io.wcm.devops.conga.model.reader.ModelReader;
import io.wcm.devops.conga.model.reader.RoleReader;
import io.wcm.devops.conga.model.role.Role;
import io.wcm.devops.conga.model.role.RoleFile;
import io.wcm.devops.conga.resource.Resource;

/**
 * Ensures that all template files references in role definitions point to an existing template.
 */
public final class RoleTemplateFileValidator implements DefinitionValidator<Void> {

  private final ModelReader<Role> modelReader = new RoleReader();

  private final HandlebarsManager handlebarsManager;

  /**
   * @param handlebarsManager Handlebars Manager
   */
  public RoleTemplateFileValidator(HandlebarsManager handlebarsManager) {
    this.handlebarsManager = handlebarsManager;
  }

  @Override
  public Void validate(Resource resource, String pathForLog) throws MojoFailureException {
    try {
      Role role = modelReader.read(resource);
      for (RoleFile roleFile : role.getFiles()) {

        // validate template file
        String templateFile = FileUtil.getTemplatePath(role, roleFile);
        if (StringUtils.isNotEmpty(templateFile)) {
          Handlebars handlebars = handlebarsManager.get(NoneEscapingStrategy.NAME, roleFile.getCharset());
          handlebars.compile(templateFile);
        }

      }
    }
    /*CHECKSTYLE:OFF*/ catch (Exception ex) { /*CHECKSTYLE:ON*/
      throw new MojoFailureException("Role definition " + pathForLog + " is invalid:\n" + ex.getMessage());
    }
    return null;
  }

}
