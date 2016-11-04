/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2016 wcm.io
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
package io.wcm.devops.conga.generator.spi.export.context;

import java.util.LinkedHashSet;
import java.util.Set;

import io.wcm.devops.conga.generator.spi.context.FileContext;

/**
 * Contains information about a generated file.
 */
public final class GeneratedFileContext {

  private FileContext fileContext;
  private final Set<String> postProcessors = new LinkedHashSet<>();

  /**
   * @return File context
   */
  public FileContext getFileContext() {
    return this.fileContext;
  }

  /**
   * @param value File context
   * @return this
   */
  public GeneratedFileContext fileContext(FileContext value) {
    this.fileContext = value;
    return this;
  }

  /**
   * @return Post processor plugin names
   */
  public Set<String> getPostProcessors() {
    return this.postProcessors;
  }

  /**
   * @param name Post processor plugin name
   * @return this
   */
  public GeneratedFileContext postProcessor(String name) {
    this.postProcessors.add(name);
    return this;
  }

}
