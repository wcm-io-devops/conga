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

import java.util.List;

/**
 * File definition.
 */
public final class File {

  private String name;
  private String dir;
  private String template;
  private List<String> variants;
  private List<String> validators;
  private List<String> postProcessors;
  private String multiply;

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
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
    this.variants = variants;
  }

  public List<String> getValidators() {
    return this.validators;
  }

  public void setValidators(List<String> validators) {
    this.validators = validators;
  }

  public List<String> getPostProcessors() {
    return this.postProcessors;
  }

  public void setPostProcessors(List<String> postProcessors) {
    this.postProcessors = postProcessors;
  }

  public String getMultiply() {
    return this.multiply;
  }

  public void setMultiply(String multiply) {
    this.multiply = multiply;
  }

}
