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
package io.wcm.devops.conga.generator.spi.export;

import io.wcm.devops.conga.generator.spi.Plugin;
import io.wcm.devops.conga.generator.spi.export.context.NodeModelExportContext;

/**
 * Plugin that exports CONGA model information per node for external tools.
 */
public interface NodeModelExportPlugin extends Plugin {

  /**
   * Export model information.
   * @param context Context
   */
  void export(NodeModelExportContext context);

}
