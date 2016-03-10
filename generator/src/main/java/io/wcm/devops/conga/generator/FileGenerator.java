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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.github.jknack.handlebars.Template;
import com.google.common.collect.ImmutableList;

import io.wcm.devops.conga.generator.plugins.fileheader.NoneFileHeader;
import io.wcm.devops.conga.generator.plugins.validator.NoneValidator;
import io.wcm.devops.conga.generator.spi.FileHeaderPlugin;
import io.wcm.devops.conga.generator.spi.PostProcessorPlugin;
import io.wcm.devops.conga.generator.spi.ValidatorPlugin;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.FileHeaderContext;
import io.wcm.devops.conga.generator.spi.context.PostProcessorContext;
import io.wcm.devops.conga.generator.spi.context.ValidatorContext;
import io.wcm.devops.conga.generator.util.FileUtil;
import io.wcm.devops.conga.generator.util.LineEndingConverter;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.generator.util.VariableMapResolver;
import io.wcm.devops.conga.model.role.RoleFile;
import io.wcm.devops.conga.model.util.MapMerger;

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
  private final FileHeaderContext fileHeaderContext;
  private final ValidatorContext validatorContext;
  private final PostProcessorContext postProcessorContext;

  FileGenerator(File nodeDir, File file, RoleFile roleFile, Map<String, Object> config,
      Template template, PluginManager pluginManager, String version, List<String> dependencyVersions, Logger log) {
    this.nodeDir = nodeDir;
    this.file = file;
    this.roleFile = roleFile;
    this.template = template;
    this.pluginManager = pluginManager;
    this.log = log;
    this.fileContext = new FileContext().file(file).charset(roleFile.getCharset());

    this.fileHeaderContext = new FileHeaderContext()
        .commentLines(buildFileHeaderCommentLines(version, dependencyVersions));

    Logger pluginLogger = new MessagePrefixLoggerFacade(log, "    ");

    this.validatorContext = new ValidatorContext()
        .options(VariableMapResolver.resolve(MapMerger.merge(roleFile.getValidatorOptions(), config)))
        .logger(pluginLogger);

    this.postProcessorContext = new PostProcessorContext()
        .options(VariableMapResolver.resolve(MapMerger.merge(roleFile.getPostProcessorOptions(), config)))
        .logger(pluginLogger);

    this.config = VariableMapResolver.deescape(config);
  }

  /**
   * Generate comment lines for file header added to all files for which a {@link FileHeaderPlugin} is registered.
   * @param dependencyVersions List of artifact versions to include
   * @return Formatted comment lines
   */
  private List<String> buildFileHeaderCommentLines(String version, List<String> dependencyVersions) {
    List<String> lines = new ArrayList<>();

    lines.add("This configuration file is AUTO-GENERATED. Please do no change it manually.");
    lines.add("If you want to change the configuration update the environment definition");
    lines.add("and generate it again.");
    lines.add("");
    lines.add((version != null ? "Version " + version + ", generated " : "Generated ")
        + "at: " + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(new Date()));

    if (dependencyVersions != null && !dependencyVersions.isEmpty()) {
      lines.add("");
      lines.add("Dependencies:");
      lines.addAll(dependencyVersions);
    }

    return formatFileHeaderCommentLines(lines);
  }

  /**
   * Format comment lines.
   * @param lines Unformatted comment lines
   * @return Formatted comment lines
   */
  private List<String> formatFileHeaderCommentLines(List<String> lines) {
    List<String> formattedLines = new ArrayList<>();

    // create separator with same length as longest comment entry
    int maxLength = lines.stream()
        .map(entry -> entry.length())
        .max(Integer::compare).get();
    String separator = StringUtils.repeat("*", maxLength + 4);

    formattedLines.add(separator);
    formattedLines.add("");
    lines.forEach(line -> formattedLines.add("  " + line));
    formattedLines.add("");
    formattedLines.add(separator);

    return formattedLines;
  }

  public void generate() throws IOException {
    log.info("Generate file {}", getFilenameForLog(fileContext));

    File dir = file.getParentFile();
    if (!dir.exists()) {
      dir.mkdirs();
    }

    // generate file with handlebars template
    // use unix file endings by default
    try (FileOutputStream fos = new FileOutputStream(file);
        Writer fileWriter = new OutputStreamWriter(fos, roleFile.getCharset())) {
      StringWriter stringWriter = new StringWriter();
      template.apply(config, stringWriter);
      fileWriter.write(normalizeLineEndings(stringWriter.toString()));
      fileWriter.flush();
    }

    // add file header, validate and post-process generated file
    applyFileHeader(fileContext, roleFile.getFileHeader());
    applyValidation(fileContext, roleFile.getValidators());
    applyPostProcessor(fileContext);
  }

  private String normalizeLineEndings(String value) {
    // convert/normalize all line endings to unix style
    String normalizedLineEndings = LineEndingConverter.normalizeToUnix(value);
    // and then to the line-ending style as requested in the tempalte definition
    return LineEndingConverter.convertTo(normalizedLineEndings, roleFile.getLineEndings());
  }

  private void applyFileHeader(FileContext fileItem, String pluginName) {
    Stream<FileHeaderPlugin> fileHeaders;
    if (StringUtils.isEmpty(pluginName)) {
      // auto-detect matching file header plugin if none are defined
      fileHeaders = pluginManager.getAll(FileHeaderPlugin.class).stream()
          .filter(plugin -> plugin.accepts(fileItem, fileHeaderContext));
    }
    else {
      // otherwise apply selected file header plugin
      fileHeaders = Stream.of(pluginName)
          .map(name -> pluginManager.get(name, FileHeaderPlugin.class));
    }
    fileHeaders
    .filter(plugin -> !StringUtils.equals(plugin.getName(), NoneFileHeader.NAME))
    .findFirst().ifPresent(plugin -> applyFileHeader(fileItem, plugin));
  }

  private void applyFileHeader(FileContext fileItem, FileHeaderPlugin plugin) {
    log.debug("  Add {} file header to file {}", plugin.getName(), getFilenameForLog(fileItem));
    plugin.apply(fileItem, fileHeaderContext);
  }

  private void applyValidation(FileContext fileItem, List<String> pluginNames) {
    Stream<ValidatorPlugin> validators;
    if (pluginNames.isEmpty()) {
      // auto-detect matching validators if none are defined
      validators = pluginManager.getAll(ValidatorPlugin.class).stream()
          .filter(plugin -> !StringUtils.equals(plugin.getName(), NoneFileHeader.NAME))
          .filter(plugin -> plugin.accepts(fileItem, validatorContext));
    }
    else {
      // otherwise apply selected validators
      validators = pluginNames.stream()
          .map(name -> pluginManager.get(name, ValidatorPlugin.class));
    }
    validators
    .filter(plugin -> !StringUtils.equals(plugin.getName(), NoneValidator.NAME))
    .forEach(plugin -> applyValidation(fileItem, plugin));
  }

  private void applyValidation(FileContext fileItem, ValidatorPlugin plugin) {
    log.info("  Validate {} for file {}", plugin.getName(), getFilenameForLog(fileItem));
    plugin.apply(fileItem, validatorContext);
  }

  private void applyPostProcessor(FileContext fileItem) {
    roleFile.getPostProcessors().stream()
    .map(name -> pluginManager.get(name, PostProcessorPlugin.class))
    .forEach(plugin -> applyPostProcessor(fileItem, plugin));
  }

  private void applyPostProcessor(FileContext fileItem, PostProcessorPlugin plugin) {
    log.info("  Post-process {} for file {}", plugin.getName(), getFilenameForLog(fileItem));

    List<FileContext> processedFiles = plugin.apply(fileItem, postProcessorContext);

    // validate processed files
    if (processedFiles != null) {
      processedFiles.forEach(processedFile -> applyFileHeader(processedFile, (String)null));
      processedFiles.forEach(processedFile -> applyValidation(processedFile, ImmutableList.of()));
    }
  }

  private String getFilenameForLog(FileContext fileItem) {
    return StringUtils.substring(FileUtil.getCanonicalPath(fileItem), FileUtil.getCanonicalPath(nodeDir).length() + 1);
  }

}
