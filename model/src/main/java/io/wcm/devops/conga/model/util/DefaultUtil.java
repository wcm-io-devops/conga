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
package io.wcm.devops.conga.model.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Helper methods for defaulting for fallback values if a given value is null.
 */
public final class DefaultUtil {

  private DefaultUtil() {
    // static methods only
  }

  /**
   * Returns the given list, or an empty (modifiable) list if given null.
   * @param <T> Type
   * @param list Given list
   * @return List (never null)
   */
  public static <T> List<T> defaultEmptyList(List<T> list) {
    if (list != null) {

      // do not allow null entries in list
      if (list.stream().filter(Objects::isNull).findFirst().isPresent()) {
        throw new IllegalArgumentException("Null element detected in list.");
      }

      return list;
    }
    else {
      return new ArrayList<T>();
    }
  }

  /**
   * Returns the given map, or an empty (modifiable) map if given null.
   * @param <K> Key type
   * @param <V> Value type
   * @param map Given map
   * @return Map (never null)
   */
  public static <K, V> Map<K, V> defaultEmptyMap(Map<K, V> map) {
    if (map != null) {
      return map;
    }
    else {
      return new HashMap<K, V>();
    }
  }

}
