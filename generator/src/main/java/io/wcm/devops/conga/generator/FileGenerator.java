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

import io.wcm.devops.conga.generator.spi.PostProcessorPlugin;
import io.wcm.devops.conga.generator.spi.ValidatorPlugin;
import io.wcm.devops.conga.generator.util.FileUtil;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.model.role.RoleFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.stream.Stream;

import com.github.jknack.handlebars.Template;

/**
 * Generates file for one environment.
 */
class FileGenerator {

  private final File file;
  private final RoleFile roleFile;
  private final Map<String, Object> config;
  private final Template template;
  private final PluginManager pluginManager;

  public FileGenerator(File file, RoleFile roleFile, Map<String, Object> config,
      Template template, PluginManager pluginManager) {
    this.file = file;
    this.roleFile = roleFile;
    this.config = config;
    this.template = template;
    this.pluginManager = pluginManager;
  }

  public void generate() throws IOException {

    File dir = file.getParentFile();
    if (!dir.exists()) {
      dir.mkdirs();
    }

    // generate file with handlebars template
    try (FileOutputStream fos = new FileOutputStream(file);
        Writer writer = new OutputStreamWriter(fos, roleFile.getCharset())) {
      template.apply(config, writer);
      writer.flush();
    }

    // validate and post-process generated file
    validateFile();
    postProcessFile();
  }

  private void validateFile() {
    Stream<ValidatorPlugin> validators;
    if (roleFile.getValidators().isEmpty()) {
      // auto-detect matching validators if none are defined
      validators = pluginManager.getAll(ValidatorPlugin.class).stream()
          .filter(validator -> validator.accepts(file));
    }
    else {
      // otherwise apply selected validators
      validators = roleFile.getValidators().stream()
          .map(name -> pluginManager.get(name, ValidatorPlugin.class));
    }
    validators.forEach(this::validateFile);
  }

  private void validateFile(ValidatorPlugin validator) {
    if (!validator.accepts(file)) {
      throw new GeneratorException("Validator '" + validator.getName() + "' does not accept " + FileUtil.getCanonicalPath(file));
    }
    validator.validate(file);
  }

  private void postProcessFile() {
    roleFile.getPostProcessors().stream()
    .map(name -> pluginManager.get(name, PostProcessorPlugin.class))
    .forEach(this::postProcessFile);
  }

  private void postProcessFile(PostProcessorPlugin postProcessor) {
    if (!postProcessor.accepts(file)) {
      throw new GeneratorException("Post processor '" + postProcessor.getName() + "' does not accept " + FileUtil.getCanonicalPath(file));
    }
    postProcessor.postProcess(file);
  }

}
