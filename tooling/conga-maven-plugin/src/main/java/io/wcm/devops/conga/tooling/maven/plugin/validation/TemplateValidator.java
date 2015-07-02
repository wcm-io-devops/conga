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

import io.wcm.devops.conga.generator.handlebars.HandlebarsManager;
import io.wcm.devops.conga.generator.plugins.handlebars.escaping.NoneEscapingStrategy;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.resource.Resource;
import io.wcm.devops.conga.resource.ResourceCollection;
import io.wcm.devops.conga.tooling.maven.plugin.util.PathUtil;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoFailureException;

import com.github.jknack.handlebars.Handlebars;
import com.google.common.collect.ImmutableList;

/**
 * Validates Handlebars templates by compiling it.
 */
public final class TemplateValidator implements DefinitionValidator {

  private static final String FILE_EXTENSION = "hbs";

  private final ResourceCollection templateDir;
  private final HandlebarsManager handlebarsManager;

  /**
   * @param templateDir Template directory
   */
  public TemplateValidator(ResourceCollection templateDir) {
    this.templateDir = templateDir;
    this.handlebarsManager = new HandlebarsManager(ImmutableList.of(templateDir), new PluginManager());
  }

  @Override
  public void validate(Resource resource, String pathForLog) throws MojoFailureException {
    if (StringUtils.equalsIgnoreCase(resource.getFileExtension(), FILE_EXTENSION)) {
      String templatePath = StringUtils.substringAfter(PathUtil.unifySlashes(resource.getCanonicalPath()),
          PathUtil.unifySlashes(templateDir.getCanonicalPath()) + "/");
      Handlebars handlebars = handlebarsManager.get(NoneEscapingStrategy.NAME, CharEncoding.UTF_8);
      try {
        handlebars.compile(templatePath);
      }
      catch (Throwable ex) {
        throw new MojoFailureException("Template " + pathForLog + " is invalid:\n" + ex.getMessage());
      }
    }
  }

}