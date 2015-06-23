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
package io.wcm.devops.conga.generator.spi;

import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.PostProcessorContext;

import java.util.List;

/**
 * Plugin that allows to post-process a generated file.
 * Returns a list of files that where post processed and should be further processed. This can be the same file that was
 * given as input, or one or multiple other files that where generated instead.
 */
public interface PostProcessorPlugin extends FilePlugin<PostProcessorContext, List<FileContext>> {

  // methods defined in super interfaces

}
