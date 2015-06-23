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

import io.wcm.devops.conga.generator.plugins.validation.NoneValidator;
import io.wcm.devops.conga.generator.spi.PostProcessorPlugin;
import io.wcm.devops.conga.generator.spi.ValidatorPlugin;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.PostProcessorContext;
import io.wcm.devops.conga.generator.spi.context.ValidatorContext;
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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.github.jknack.handlebars.Template;

/**
 * Generates file for one environment.
 */
class FileGenerator {

  private final File nodeDir;
  private final File file;
  private final RoleFile roleFile;
  private final Map<String, Object> config;
  private final Template template;
  private final PluginManager pluginManager;
  private final Logger log;
  private final FileContext fileContext;

  public FileGenerator(File nodeDir, File file, RoleFile roleFile, Map<String, Object> config,
      Template template, PluginManager pluginManager, Logger log) {
    this.nodeDir = nodeDir;
    this.file = file;
    this.roleFile = roleFile;
    this.config = config;
    this.template = template;
    this.pluginManager = pluginManager;
    this.log = log;
    this.fileContext = new FileContext().file(file).charset(roleFile.getCharset());
  }

  public void generate() throws IOException {
    log.info("Generate file {}", getFilenameForLog());

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
          .filter(validator -> validateAccepts(validator));
    }
    else {
      // otherwise apply selected validators
      validators = roleFile.getValidators().stream()
          .map(name -> pluginManager.get(name, ValidatorPlugin.class));
    }
    validators.forEach(this::validateFile);
  }

  private boolean validateAccepts(ValidatorPlugin validator) {
    ValidatorContext validatorContext = new ValidatorContext().options(roleFile.getValidatorOptions()).logger(log);
    return validator.accepts(fileContext, validatorContext);
  }

  private void validateFile(ValidatorPlugin validator) {
    if (StringUtils.equals(validator.getName(), NoneValidator.NAME)) {
      return;
    }
    ValidatorContext validatorContext = new ValidatorContext().options(roleFile.getValidatorOptions()).logger(log);
    if (!validator.accepts(fileContext, validatorContext)) {
      throw new GeneratorException("Validator '" + validator.getName() + "' does not accept " + FileUtil.getCanonicalPath(file));
    }
    log.info("Validate {} for file {}", validator.getName(), getFilenameForLog());
    validator.validate(fileContext, validatorContext);
  }

  private void postProcessFile() {
    roleFile.getPostProcessors().stream()
    .map(name -> pluginManager.get(name, PostProcessorPlugin.class))
    .forEach(this::postProcessFile);
  }

  private void postProcessFile(PostProcessorPlugin postProcessor) {
    PostProcessorContext postProcessorContext = new PostProcessorContext().options(roleFile.getValidatorOptions()).logger(log);
    if (!postProcessor.accepts(fileContext, postProcessorContext)) {
      throw new GeneratorException("Post processor '" + postProcessor.getName() + "' does not accept " + FileUtil.getCanonicalPath(file));
    }
    log.info("Post-process {} for file {}", postProcessor.getName(), getFilenameForLog());
    postProcessor.postProcess(fileContext, postProcessorContext);
  }

  private String getFilenameForLog() {
    return StringUtils.substring(FileUtil.getCanonicalPath(file), FileUtil.getCanonicalPath(nodeDir).length() + 1);
  }

}
