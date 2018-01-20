/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2018 wcm.io
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

import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoFailureException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import io.wcm.devops.conga.generator.util.VariableStringResolver;
import io.wcm.devops.conga.model.reader.AbstractModelReader;
import io.wcm.devops.conga.model.reader.ModelReader;
import io.wcm.devops.conga.resource.Resource;

/**
 * Ensures that nor value providers are used in role models. They should only be used in environments.
 */
public final class NoValueProviderInRoleValidator implements DefinitionValidator<Void> {

  private final ModelReader<Map> mapReader = new MapReader();

  @Override
  public Void validate(Resource resource, String pathForLog) throws MojoFailureException {
    try {
      // iterate over whole value and validate all string values that are found
      process(mapReader.read(resource));
    }
    /*CHECKSTYLE:OFF*/ catch (Exception ex) { /*CHECKSTYLE:ON*/
      throw new MojoFailureException("Role definition " + pathForLog + " is invalid:\n" + ex.getMessage());
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  private void process(Object value) {
    if (value instanceof String) {
      validate((String)value);
    }
    else if (value instanceof Map) {
      ((Map)value).values().forEach(this::process);
    }
    else if (value instanceof List) {
      ((List)value).forEach(this::process);
    }
  }

  private void validate(String value) {
    if (VariableStringResolver.hasValueProviderReference(value)) {
      throw new RuntimeException("Role definitions must not reference value providers: " + value);
    }
  }

  private static class MapReader extends AbstractModelReader<Map> {
    MapReader() {
      super(getYaml());
    }
    private static Yaml getYaml() {
      Constructor constructor = new Constructor(Map.class);
      return new Yaml(constructor);
    }
  }


}