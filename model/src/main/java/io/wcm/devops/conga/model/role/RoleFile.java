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
package io.wcm.devops.conga.model.role;

import static io.wcm.devops.conga.model.util.DefaultUtil.defaultEmptyList;
import static io.wcm.devops.conga.model.util.DefaultUtil.defaultEmptyMap;
import io.wcm.devops.conga.model.shared.AbstractModel;
import io.wcm.devops.conga.model.shared.LineEndings;
import io.wcm.devops.conga.model.util.MapExpander;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.CharEncoding;

/**
 * Defines a file to be generated for a role.
 */
public final class RoleFile extends AbstractModel {

  private String file;
  private String dir;
  private String template;
  private List<String> variants = new ArrayList<>();
  private String condition;
  private List<String> validators = new ArrayList<>();
  private Map<String, Object> validatorOptions = new HashMap<>();
  private List<String> postProcessors = new ArrayList<>();
  private Map<String, Object> postProcessorOptions = new HashMap<>();
  private String fileHeader;
  private String multiply;
  private Map<String, Object> multiplyOptions = new HashMap<>();
  private String charset = CharEncoding.UTF_8;
  private LineEndings lineEndings = LineEndings.unix;
  private String escapingStrategy;

  /**
   * Defines the file name of the generated file.
   * Variable placeholders can be used to reference variables or context properties, e.g. <code>${tenant}</code> for the
   * current tenant name if a tenant multiply plugin is used.
   * @return File name
   */
  public String getFile() {
    return this.file;
  }

  public void setFile(String name) {
    this.file = name;
  }

  /**
   * Sets the directory to generate the file in. The directory name is relative to the configuration target directory of
   * the node.
   * Variable placeholders can be used to reference variables or context properties, e.g. <code>${tenant}</code> for the
   * current tenant name if a tenant multiply plugin is used.
   * @return Directory name
   */
  public String getDir() {
    return this.dir;
  }

  public void setDir(String dir) {
    this.dir = dir;
  }

  /**
   * Defines the template name to be used to generate the file. The template file should have an ".hbs" extension.
   * The file name is resolved relative to the template directory of the role.
   * @return Template file name
   */
  public String getTemplate() {
    return this.template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  /**
   * Defines the role variant names for which this file should be generated.
   * If no names are defined the file is generated for all role variants.
   * @return List of role variant names
   */
  public List<String> getVariants() {
    return this.variants;
  }

  public void setVariants(List<String> variants) {
    this.variants = defaultEmptyList(variants);
  }

  /**
   * Defines a condition whether the file should be generated or not. The condition contains one single variable
   * placeholder reference, e.g. <code>${myvar}</code>. The condition is true if the resulting string of the variable is
   * not empty and does not match "false".
   * @return Condition expression
   */
  public String getCondition() {
    return this.condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  /**
   * Defines the name of a file header plugin that should be applied to the generated file.
   * If no name is given the best-matching plugin is detected automatically by the file extension.
   * If this is not wished, explicitly setting the property to "none" ensures that no plugin is applied.
   * @return File header plugin name
   */
  public String getFileHeader() {
    return this.fileHeader;
  }

  public void setFileHeader(String fileHeader) {
    this.fileHeader = fileHeader;
  }

  /**
   * Defines a list of validator plugin names that should be applied to the generated file.
   * If none is given the best-matching plugins are detected automatically by the file extension.
   * If this is not wished, explicitly setting a list entry with "none" ensures that no plugin is applied.
   * @return List of validator plugin names
   */
  public List<String> getValidators() {
    return this.validators;
  }

  public void setValidators(List<String> validators) {
    this.validators = defaultEmptyList(validators);
  }

  /**
   * Defines configuration parameters that are passed as options to the validator plugins.
   * They are merged with the configuration parameters from the configuration inheritance tree.
   * @return Configuration parameters
   */
  public Map<String, Object> getValidatorOptions() {
    return this.validatorOptions;
  }


  public void setValidatorOptions(Map<String, Object> validatorOptions) {
    this.validatorOptions = defaultEmptyMap(MapExpander.expand(validatorOptions));
  }

  /**
   * Defines a list of post processor plugin names that should be applied to the generated file.
   * @return List of post processor plugin names
   */
  public List<String> getPostProcessors() {
    return this.postProcessors;
  }

  public void setPostProcessors(List<String> postProcessors) {
    this.postProcessors = defaultEmptyList(postProcessors);
  }

  /**
   * Defines configuration parameters that are passed as options to the post processor plugins.
   * They are merged with the configuration parameters from the configuration inheritance tree.
   * @return Configuration parameters
   */
  public Map<String, Object> getPostProcessorOptions() {
    return this.postProcessorOptions;
  }

  public void setPostProcessorOptions(Map<String, Object> postProcessorOptions) {
    this.postProcessorOptions = defaultEmptyMap(MapExpander.expand(postProcessorOptions));
  }

  /**
   * Defines a multiply plugin name to be used. Using a mulitply plugin multiple files are generated instead
   * of a single file, e.g. one per tenant. To ensure each file has a unique file name variables have to be used
   * in the "file" property.
   * @return Multiply plugin name
   */
  public String getMultiply() {
    return this.multiply;
  }

  public void setMultiply(String multiply) {
    this.multiply = multiply;
  }

  /**
   * Defines configuration parameters that are passed as options to the multiply plugin.
   * They are merged with the configuration parameters from the configuration inheritance tree.
   * @return Configuration parameters
   */
  public Map<String, Object> getMultiplyOptions() {
    return this.multiplyOptions;
  }

  public void setMultiplyOptions(Map<String, Object> multiplyOptions) {
    this.multiplyOptions = defaultEmptyMap(MapExpander.expand(multiplyOptions));
  }

  /**
   * Defines a charset to be used for the generated file. It is expected that the templates is encoded in the same
   * charset. If not set UTF-8 is the default charset.
   * @return Charset name
   */
  public String getCharset() {
    return this.charset;
  }

  public void setCharset(String charset) {
    this.charset = charset;
  }

  /**
   * Defines the line endings style for the generated files. Possible values are 'unix', 'windows' and 'macos'.
   * If not set 'unix' line endings are the default.
   * @return Line endings style
   */
  public LineEndings getLineEndings() {
    return this.lineEndings;
  }

  public void setLineEndings(LineEndings lineEndings) {
    this.lineEndings = lineEndings;
  }

  /**
   * Defines an handlebars escaping strategy plugin to be used.
   * If no name is given the best-matching plugin is detected automatically by the file extension.
   * If this is not wished, explicitly setting the property to "none" ensures that no plugin is applied.
   * @return Handlebars escaping strategy plugin name.
   */
  public String getEscapingStrategy() {
    return this.escapingStrategy;
  }

  public void setEscapingStrategy(String escapingStrategy) {
    this.escapingStrategy = escapingStrategy;
  }

}
