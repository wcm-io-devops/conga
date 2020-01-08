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
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.github.jknack.handlebars.Template;
import com.google.common.collect.ImmutableList;

import io.wcm.devops.conga.generator.plugins.fileheader.NoneFileHeader;
import io.wcm.devops.conga.generator.plugins.validator.NoneValidator;
import io.wcm.devops.conga.generator.spi.FileHeaderPlugin;
import io.wcm.devops.conga.generator.spi.FilePlugin;
import io.wcm.devops.conga.generator.spi.ImplicitApplyOptions;
import io.wcm.devops.conga.generator.spi.PostProcessorPlugin;
import io.wcm.devops.conga.generator.spi.ValidatorPlugin;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.FileHeaderContext;
import io.wcm.devops.conga.generator.spi.context.PluginContextOptions;
import io.wcm.devops.conga.generator.spi.context.PostProcessorContext;
import io.wcm.devops.conga.generator.spi.context.ValidatorContext;
import io.wcm.devops.conga.generator.spi.export.context.GeneratedFileContext;
import io.wcm.devops.conga.generator.util.FileUtil;
import io.wcm.devops.conga.generator.util.LineEndingConverter;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.generator.util.VariableMapResolver;
import io.wcm.devops.conga.model.role.RoleFile;
import io.wcm.devops.conga.model.util.MapMerger;

/**
 * Generates file for one environment.
 */
@SuppressWarnings("PMD.MoreThanOneLogger")
class FileGenerator {

  private final String environmentName;
  private final String roleName;
  private final List<String> roleVariantNames;
  private final String templateName;
  private final File nodeDir;
  private final File file;
  private final String url;
  private final RoleFile roleFile;
  private final Map<String, Object> config;
  private final Template template;
  private final PluginManager pluginManager;
  private final UrlFileManager urlFileManager;
  private final Logger log;
  private final FileContext fileContext;
  private final FileHeaderContext fileHeaderContext;
  private final ValidatorContext validatorContext;
  private final PostProcessorContext postProcessorContext;
  private final boolean allowSymlinks;

  // if the creation of a symlink fails once, do not try it again (esp. e.g. on windows systems)
  private static boolean symlinkCreationFailed;

  //CHECKSTYLE:OFF
  FileGenerator(GeneratorOptions options, String environmentName,
      String roleName, List<String> roleVariantNames, String templateName,
      File nodeDir, File file, String url, RoleFile roleFile, Map<String, Object> config, Template template,
      VariableMapResolver variableMapResolver, UrlFileManager urlFileManager, PluginContextOptions pluginContextOptions,
      Collection<String> dependencyVersions) {
    //CHECKSTYLE:ON
    this.environmentName = environmentName;
    this.roleName = roleName;
    this.roleVariantNames = roleVariantNames;
    this.templateName = templateName;
    this.nodeDir = nodeDir;
    this.file = file;
    this.url = url;
    this.roleFile = roleFile;
    this.template = template;
    this.pluginManager = options.getPluginManager();
    this.urlFileManager = urlFileManager;
    this.log = options.getLogger();
    this.fileContext = new FileContext()
        .file(file)
        .charset(roleFile.getCharset())
        .modelOptions(roleFile.getModelOptions())
        .targetDir(nodeDir);

    // overlay logger in options with plugin-specific logger
    Logger pluginLogger = new MessagePrefixLoggerFacade(log, "    ");
    PluginContextOptions pluginContextOptionsForPlugin = new PluginContextOptions()
        .pluginContextOptions(pluginContextOptions)
        .logger(pluginLogger);


    this.fileHeaderContext = new FileHeaderContext()
        .pluginContextOptions(pluginContextOptionsForPlugin)
        .commentLines(buildFileHeaderCommentLines(options.getVersion(), dependencyVersions));

    this.validatorContext = new ValidatorContext()
        .pluginContextOptions(pluginContextOptionsForPlugin)
        .options(variableMapResolver.resolve(MapMerger.merge(roleFile.getValidatorOptions(), config)));

    this.postProcessorContext = new PostProcessorContext()
        .pluginContextOptions(pluginContextOptionsForPlugin)
        .options(variableMapResolver.resolve(MapMerger.merge(roleFile.getPostProcessorOptions(), config)));

    this.config = variableMapResolver.deescape(config);
    this.allowSymlinks = options.isAllowSymlinks();
  }

  /**
   * Generate comment lines for file header added to all files for which a {@link FileHeaderPlugin} is registered.
   * @param dependencyVersions List of artifact versions to include
   * @return Formatted comment lines
   */
  private List<String> buildFileHeaderCommentLines(String version, Collection<String> dependencyVersions) {
    List<String> lines = new ArrayList<>();

    lines.add("This file is AUTO-GENERATED by CONGA. Please do not change it manually.");
    lines.add("");
    if (version != null) {
      lines.add("Version " + version);
    }

    // add information how this file was generated
    lines.add("Environment: " + environmentName);
    lines.add("Role: " + roleName);
    if (!roleVariantNames.isEmpty()) {
      lines.add("Variant: " + StringUtils.join(roleVariantNames, ","));
    }
    lines.add("Template: " + templateName);

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

  /**
   * Generate file(s).
   * @return List of files that where generated directly or indirectly (by post processors).
   */
  public Collection<GeneratedFileContext> generate() throws IOException {
    File dir = file.getParentFile();
    if (!dir.exists()) {
      dir.mkdirs();
    }

    Collection<GeneratedFileContext> postProcessedFiles;
    if (template != null) {
      log.info("Generate file {}", getFilenameForLog(fileContext));

      // generate with template
      generateWithTemplate();

      // add file header, validate and post-process generated file
      applyFileHeader(fileContext, roleFile.getFileHeader());
      applyValidation(fileContext, roleFile.getValidators());
      postProcessedFiles = applyPostProcessor(fileContext);

    }
    else if (StringUtils.isNotBlank(url)) {

      boolean symlinkCreated = false;
      if (allowSymlinks && !symlinkCreationFailed && urlFileManager.isLocalFile(url)) {
        log.info("Symlink file {} from {}", getFilenameForLog(fileContext), url);
        if (createSymlinkToLocalFile()) {
          symlinkCreated = true;
        }
        else {
          symlinkCreationFailed = true;
        }
      }

      // generate by downloading/copying from URL
      if (!symlinkCreated) {
        log.info("Copy file {} from {}", getFilenameForLog(fileContext), url);
        copyFromUrlFile();
      }

      // post-process downloaded or symlinked file
      postProcessedFiles = applyPostProcessor(fileContext);
    }
    else {
      throw new IOException("No template and nor URL defined for file: " + FileUtil.getFileInfo(roleName, roleFile));
    }

    return postProcessedFiles;
  }

  /**
   * Generate file with handlebars template.
   * Use unix file endings by default.
   */
  private void generateWithTemplate() throws IOException {
    try (FileOutputStream fos = new FileOutputStream(file);
        Writer fileWriter = new OutputStreamWriter(fos, roleFile.getCharset())) {
      StringWriter stringWriter = new StringWriter();
      template.apply(config, stringWriter);
      fileWriter.write(normalizeLineEndings(stringWriter.toString()));
      fileWriter.flush();
    }
  }

  /**
   * Generate file by downloading/copying it's binary content from URL.
   */
  private void copyFromUrlFile() throws IOException {
    try (FileOutputStream fos = new FileOutputStream(file);
        InputStream is = urlFileManager.getFile(url)) {
      IOUtils.copy(is, fos);
      fos.flush();
    }
  }

  /**
   * Create a symlink to a local file.
   * @return true if symlink creation was successful. This is likely to return false on windows operating systems.
   * @throws IOException If an unexpected error occurs
   */
  @SuppressWarnings("PMD.GuardLogStatement")
  private boolean createSymlinkToLocalFile() throws IOException {
    // if file is a local file try to create a symlink to it
    File localFile = urlFileManager.getLocalFile(url);
    Path targetPath = file.toPath();
    Path sourcePath = localFile.toPath();
    try {
      Files.createSymbolicLink(targetPath, sourcePath);
      return true;
    }
    catch (IOException ex) {
      // creates symbolic link failed - log warning and fallback to copying content
      log.warn("Unable to create symbolic link at " + FileUtil.getCanonicalPath(file) + ": " + ex.getMessage());
      return false;
    }
  }

  private String normalizeLineEndings(String value) {
    // convert/normalize all line endings to unix style
    String normalizedLineEndings = LineEndingConverter.normalizeToUnix(value);
    // and then to the line-ending style as requested in the tempalte definition
    return LineEndingConverter.convertTo(normalizedLineEndings, roleFile.getLineEndings());
  }

  /**
   * Collect all file plugins that are either configured explicitely, or apply implicitely, or should always apply.
   * @param pluginClass File plugin class
   * @param fileItem File item
   * @param contextObject File plugin context object
   * @param pluginNames List of configured plugin names
   * @return List of plugins that should apply
   * @param <T> Plugin context object type.
   * @param <R> Return type of the plugin apply method.
   * @param <P> Plugin class
   */
  private <T, R, P extends FilePlugin<T, R>> Stream<P> collectFilePlugins(Class<P> pluginClass, FileContext fileItem, T contextObject,
      List<String> pluginNames) {
    Stream<P> plugins;
    if (pluginNames.isEmpty()) {
      // auto-detect matching plugins if none are defined
      plugins = pluginManager.getAll(pluginClass).stream()
          .filter(plugin -> plugin.accepts(fileItem, contextObject))
          .filter(plugin -> plugin.implicitApply(fileItem, contextObject) == ImplicitApplyOptions.WHEN_UNCONFIGURED);
    }
    else {
      // otherwise apply selected plugins
      plugins = pluginNames.stream()
          .map(name -> pluginManager.get(name, pluginClass));
    }
    // add plugins that should always apply
    return Stream.concat(plugins, pluginManager.getAll(pluginClass).stream()
          .filter(plugin -> plugin.accepts(fileItem, contextObject))
          .filter(plugin -> plugin.implicitApply(fileItem, contextObject) == ImplicitApplyOptions.ALWAYS));
  }

  private void applyFileHeader(FileContext fileItem, String pluginName) {
    List<String> pluginNames = new ArrayList<>();
    if (!StringUtils.isEmpty(pluginName)) {
      pluginNames.add(pluginName);
    }
    collectFilePlugins(FileHeaderPlugin.class, fileItem, fileHeaderContext, pluginNames)
        .filter(plugin -> !StringUtils.equals(plugin.getName(), NoneFileHeader.NAME))
        .forEach(plugin -> applyFileHeader(fileItem, plugin));
  }

  private void applyFileHeader(FileContext fileItem, FileHeaderPlugin plugin) {
    log.debug("  Add {} file header to file {}", plugin.getName(), getFilenameForLog(fileItem));
    plugin.apply(fileItem, fileHeaderContext);
  }

  private void applyValidation(FileContext fileItem, List<String> pluginNames) {
    collectFilePlugins(ValidatorPlugin.class, fileItem, validatorContext, pluginNames)
        .filter(plugin -> !StringUtils.equals(plugin.getName(), NoneValidator.NAME))
        .forEach(plugin -> applyValidation(fileItem, plugin));
  }

  private void applyValidation(FileContext fileItem, ValidatorPlugin plugin) {
    log.info("  Validate {} for file {}", plugin.getName(), getFilenameForLog(fileItem));
    plugin.apply(fileItem, validatorContext);
  }

  private Collection<GeneratedFileContext> applyPostProcessor(FileContext fileItem) {
    // collect distinct list of files returned by each post processor
    // if a file is returned by multiple post processors combine to single entry with multiple plugin names
    Map<String, GeneratedFileContext> consolidatedFiles = new LinkedHashMap<>();

    // start with original file
    consolidatedFiles.put(fileContext.getCanonicalPath(), new GeneratedFileContext().fileContext(fileContext));

    // collect postprocessor plugins
    Stream<PostProcessorPlugin> postProcessors = collectFilePlugins(PostProcessorPlugin.class, fileItem, postProcessorContext,
        roleFile.getPostProcessors());

    // process all processors. if multiple processors each processor processed the files of the previous one.
    postProcessors.forEach(plugin -> applyPostProcessor(consolidatedFiles, plugin));

    return consolidatedFiles.values();
  }

  private void applyPostProcessor(Map<String, GeneratedFileContext> consolidatedFiles, PostProcessorPlugin plugin) {

    // process all files from given map
    ImmutableList.copyOf(consolidatedFiles.values()).stream()
        // do not apply post processor twice
        .filter(fileItem -> !fileItem.getPostProcessors().contains(plugin.getName()))
        .filter(fileItem -> plugin.accepts(fileItem.getFileContext(), postProcessorContext))
        .forEach(fileItem -> {
          List<FileContext> processedFiles = applyPostProcessor(fileItem.getFileContext(), plugin);
          fileItem.postProcessor(plugin.getName());
          processedFiles.forEach(item -> {
            GeneratedFileContext generatedFileContext = consolidatedFiles.get(item.getCanonicalPath());
            if (generatedFileContext == null) {
              generatedFileContext = new GeneratedFileContext().fileContext(item);
              consolidatedFiles.put(item.getCanonicalPath(), generatedFileContext);
            }
            generatedFileContext.postProcessor(plugin.getName());
          });
      });

    // remove items that do no longer exist
    ImmutableList.copyOf(consolidatedFiles.values()).forEach(fileItem -> {
      if (!fileItem.getFileContext().getFile().exists()) {
        consolidatedFiles.remove(fileItem.getFileContext().getCanonicalPath());
      }
    });

    // apply post processor configured as implicit ALWAYS
    consolidatedFiles.values().forEach(fileItem -> {
      pluginManager.getAll(PostProcessorPlugin.class).stream()
          .filter(implicitPlugin -> implicitPlugin.accepts(fileItem.getFileContext(), postProcessorContext))
          .filter(implicitPlugin -> implicitPlugin.implicitApply(fileItem.getFileContext(), postProcessorContext) == ImplicitApplyOptions.ALWAYS)
          // do not apply post processor twice
          .filter(implicitPlugin -> !fileItem.getPostProcessors().contains(implicitPlugin.getName()))
          .forEach(implicitPlugin -> {
            List<FileContext> processedFiles = applyPostProcessor(fileItem.getFileContext(), implicitPlugin);
            fileItem.postProcessor(implicitPlugin.getName());
            processedFiles.forEach(item -> {
              GeneratedFileContext generatedFileContext = consolidatedFiles.get(item.getCanonicalPath());
              if (generatedFileContext == null) {
                generatedFileContext = new GeneratedFileContext().fileContext(item);
                consolidatedFiles.put(item.getCanonicalPath(), generatedFileContext);
              }
              generatedFileContext.postProcessor(implicitPlugin.getName());
            });
          });
    });

    // remove items that do no longer exist
    ImmutableList.copyOf(consolidatedFiles.values()).forEach(fileItem -> {
      if (!fileItem.getFileContext().getFile().exists()) {
        consolidatedFiles.remove(fileItem.getFileContext().getCanonicalPath());
      }
    });

  }

  private List<FileContext> applyPostProcessor(FileContext fileItem, PostProcessorPlugin plugin) {
    log.info("  Post-process {} for file {}", plugin.getName(), getFilenameForLog(fileItem));

    List<FileContext> processedFiles = plugin.apply(fileItem, postProcessorContext);

    if (processedFiles != null) {
      // add file header, validate files
      processedFiles.forEach(processedFile -> {
            applyFileHeader(processedFile, (String)null);
            applyValidation(processedFile, ImmutableList.of());
      });
    }

    return processedFiles;
  }

  private String getFilenameForLog(FileContext fileItem) {
    return StringUtils.substring(fileItem.getCanonicalPath(), FileUtil.getCanonicalPath(nodeDir).length() + 1);
  }

}
