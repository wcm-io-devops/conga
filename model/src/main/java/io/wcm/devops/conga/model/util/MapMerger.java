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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Deep merges two maps.
 */
public final class MapMerger {

  /**
   * Special list entry the results in merging two lists inside a map instead of keeping only the list from the first
   * map.
   */
  static final String LIST_MERGE_ENTRY = "_merge_";

  private MapMerger() {
    // static methods only
  }

  /**
   * Deep merges two maps. Map1 has higher priority.
   * @param <K> Key type
   * @param map1 Map 1
   * @param map2 Map 2
   * @return Merged map
   */
  @SuppressWarnings("unchecked")
  public static <K> Map<K, Object> merge(Map<K, Object> map1, Map<K, Object> map2) {
    Map<K, Object> merged = new HashMap<>();
    if (map1 == null || map2 == null) {
      if (map1 != null) {
        merged.putAll(map1);
      }
      if (map2 != null) {
        merged.putAll(map2);
      }
      return merged;
    }

    Set<K> allKeys = new HashSet<>();
    allKeys.addAll(map1.keySet());
    allKeys.addAll(map2.keySet());

    for (K key : allKeys) {
      Object v1 = map1.get(key);
      Object v2 = map2.get(key);
      if (v1 instanceof Map || v2 instanceof Map) {
        Map<K, Object> m1 = v1 instanceof Map ? (Map<K, Object>)v1 : null;
        Map<K, Object> m2 = v2 instanceof Map ? (Map<K, Object>)v2 : null;
        merged.put(key, merge(m1, m2));
      }
      else if (v1 instanceof List && v2 instanceof List) {
        List<Object> l1 = (List<Object>)v1;
        List<Object> l2 = (List<Object>)v2;
        boolean l1Mergeable = isMergeable(l1);
        boolean l2Mergeable = isMergeable(l2);
        if (l1Mergeable || l2Mergeable) {
          List<Object> mergedList;
          if (l2Mergeable && !l1Mergeable) {
            mergedList = mergeList(l2, l1);
          }
          else {
            mergedList = mergeList(l1, l2);
          }
          merged.put(key, mergedList);
        }
        else {
          merged.put(key, l1);
        }
      }
      else if (v1 != null) {
        merged.put(key, v1);
      }
      else {
        merged.put(key, v2);
      }
    }

    return merged;
  }

  private static boolean isMergeable(List<Object> list) {
    return (list instanceof ArrayListWithMerge) || list.contains(LIST_MERGE_ENTRY);
  }

  /**
   * Merges l2 into l1 at the position where the _merge_ keyword is located in l1.
   * @param l1 List 1
   * @param l2 List 2
   * @return Merged list
   */
  private static List<Object> mergeList(List<Object> l1, List<Object> l2) {
    List<Object> mergedList = new ArrayListWithMerge<>();
    boolean hasMergeToken = l1.contains(LIST_MERGE_ENTRY);
    if (!hasMergeToken) {
      l2.forEach(item2 -> addIfNotContainedNoToken(mergedList, item2));
    }
    for (Object item1 : l1) {
      if (LIST_MERGE_ENTRY.equals(item1)) {
        l2.forEach(item2 -> addIfNotContainedNoToken(mergedList, item2));
      }
      else {
        addIfNotContainedNoToken(mergedList, item1);
      }
    }
    return mergedList;
  }

  /**
   * Add item to given list if item is not already contained in this list.
   * @param item Item
   */
  private static void addIfNotContainedNoToken(List<Object> list, Object item) {
    if (!LIST_MERGE_ENTRY.equals(item) && !list.contains(item)) {
      list.add(item);
    }
  }

}
