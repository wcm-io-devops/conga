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

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoFailureException;

import com.github.jknack.handlebars.Handlebars;

import io.wcm.devops.conga.generator.handlebars.HandlebarsManager;
import io.wcm.devops.conga.generator.plugins.handlebars.escaping.NoneEscapingStrategy;
import io.wcm.devops.conga.resource.Resource;
import io.wcm.devops.conga.resource.ResourceCollection;
import io.wcm.devops.conga.tooling.maven.plugin.util.PathUtil;

/**
 * Validates Handlebars templates by compiling it.
 */
public final class TemplateValidator implements DefinitionValidator<Void> {

  private static final String FILE_EXTENSION = "hbs";

  private final ResourceCollection templateDir;
  private final HandlebarsManager handlebarsManager;

  /**
   * @param templateDir Template directory
   * @param handlebarsManager Handlebars Manager
   */
  public TemplateValidator(ResourceCollection templateDir, HandlebarsManager handlebarsManager) {
    this.templateDir = templateDir;
    this.handlebarsManager = handlebarsManager;
  }

  @Override
  public Void validate(Resource resource, String pathForLog) throws MojoFailureException {
    if (StringUtils.equalsIgnoreCase(resource.getFileExtension(), FILE_EXTENSION)) {
      String templatePath = StringUtils.substringAfter(PathUtil.unifySlashes(resource.getCanonicalPath()),
          PathUtil.unifySlashes(templateDir.getCanonicalPath()) + "/");
      Handlebars handlebars = handlebarsManager.get(NoneEscapingStrategy.NAME, StandardCharsets.UTF_8.name());
      try {
        handlebars.compile(templatePath);
      }
      /*CHECKSTYLE:OFF*/ catch (Exception ex) { /*CHECKSTYLE:ON*/
        throw new MojoFailureException("Template " + pathForLog + " is invalid:\n" + ex.getMessage());
      }
    }
    return null;
  }

}
