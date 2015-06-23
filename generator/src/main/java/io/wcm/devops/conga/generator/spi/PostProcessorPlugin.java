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
 */
public interface PostProcessorPlugin extends FilePlugin<PostProcessorContext, List<FileContext>> {

  /**
   * Applies the post processing.
   * @param file Context file
   * @param context Context objects
   * @return Returns a list of files that where generated additionally or instead of the input file.
   *         The input file itself should never be returned, otherwise files may get processed twice.
   */
  @Override
  List<FileContext> apply(FileContext file, PostProcessorContext context);

}
