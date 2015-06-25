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
import io.wcm.devops.conga.model.util.MapExpander;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.CharEncoding;

/**
 * File definition.
 */
public final class RoleFile implements Cloneable {

  private String file;
  private String dir;
  private String template;
  private List<String> variants = new ArrayList<>();
  private List<String> validators = new ArrayList<>();
  private Map<String, Object> validatorOptions = new HashMap<>();
  private List<String> postProcessors = new ArrayList<>();
  private Map<String, Object> postProcessorOptions = new HashMap<>();
  private String fileHeader;
  private String multiply;
  private Map<String, Object> multiplyOptions = new HashMap<>();
  private String charset = CharEncoding.UTF_8;

  public String getFile() {
    return this.file;
  }

  public void setFile(String name) {
    this.file = name;
  }

  public String getDir() {
    return this.dir;
  }

  public void setDir(String dir) {
    this.dir = dir;
  }

  public String getTemplate() {
    return this.template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  public List<String> getVariants() {
    return this.variants;
  }

  public void setVariants(List<String> variants) {
    this.variants = defaultEmptyList(variants);
  }

  public String getFileHeader() {
    return this.fileHeader;
  }

  public void setFileHeader(String fileHeader) {
    this.fileHeader = fileHeader;
  }

  public List<String> getValidators() {
    return this.validators;
  }

  public void setValidators(List<String> validators) {
    this.validators = defaultEmptyList(validators);
  }

  public Map<String, Object> getValidatorOptions() {
    return this.validatorOptions;
  }


  public void setValidatorOptions(Map<String, Object> validatorOptions) {
    this.validatorOptions = defaultEmptyMap(MapExpander.expand(validatorOptions));
  }

  public List<String> getPostProcessors() {
    return this.postProcessors;
  }

  public void setPostProcessors(List<String> postProcessors) {
    this.postProcessors = defaultEmptyList(postProcessors);
  }

  public Map<String, Object> getPostProcessorOptions() {
    return this.postProcessorOptions;
  }

  public void setPostProcessorOptions(Map<String, Object> postProcessorOptions) {
    this.postProcessorOptions = defaultEmptyMap(MapExpander.expand(postProcessorOptions));
  }

  public String getMultiply() {
    return this.multiply;
  }

  public void setMultiply(String multiply) {
    this.multiply = multiply;
  }

  public Map<String, Object> getMultiplyOptions() {
    return this.multiplyOptions;
  }

  public void setMultiplyOptions(Map<String, Object> multiplyOptions) {
    this.multiplyOptions = defaultEmptyMap(MapExpander.expand(multiplyOptions));
  }

  public String getCharset() {
    return this.charset;
  }

  public void setCharset(String charset) {
    this.charset = charset;
  }

}
