/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 wcm.io
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
package io.wcm.devops.conga.model.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Special ArrayList that marks a list as "mergeable" in downstream.
 * @param <T> List type
 */
final class ArrayListWithMerge<T> extends ArrayList<T> {
  private static final long serialVersionUID = 1L;

  /**
   * Add all items of given list if item is not already contained in this list.
   * @param list Given list
   */
  public void addAllIfNotContained(List<T> list) {
    for (T item : list) {
      if (!this.contains(item)) {
        this.add(item);
      }
    }
  }

}
