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
package io.wcm.devops.conga.generator.spi.yaml.context;

import io.wcm.devops.conga.generator.spi.context.AbstractPluginContext;

/**
 * Context for {@link io.wcm.devops.conga.generator.spi.export.NodeModelExportPlugin}.
 */
public final class YamlRepresentContext extends AbstractPluginContext<YamlRepresentContext> {

  private YamlRepresenter yamlRepresenter;

  /**
   * @return YAML representer
   */
  public YamlRepresenter getYamlRepresenter() {
    return this.yamlRepresenter;
  }

  /**
   * @param value YAML representer
   * @return this
   */
  public YamlRepresentContext yamlRepresenter(YamlRepresenter value) {
    this.yamlRepresenter = value;
    return this;
  }

}
