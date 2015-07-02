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

import io.wcm.devops.conga.model.reader.ModelReader;
import io.wcm.devops.conga.resource.Resource;

import org.apache.maven.plugin.MojoFailureException;

/**
 * Validates YAML model file against it's reader.
 */
public final class ModelValidator implements DefinitionValidator {

  private final String modelName;
  private final ModelReader<?> modelReader;

  /**
   * @param modelName Model name (for log message)
   * @param modelReader Model reader implementation
   */
  public ModelValidator(String modelName, ModelReader<?> modelReader) {
    this.modelName = modelName;
    this.modelReader = modelReader;
  }

  @Override
  public void validate(Resource resource, String pathForLog) throws MojoFailureException {
    try {
      modelReader.read(resource);
    }
    catch (Throwable ex) {
      throw new MojoFailureException(modelName + " definition " + pathForLog + " is invalid:\n" + ex.getMessage());
    }
  }

}