/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2020 wcm.io
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
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

/**
 * Splits up maps.
 */
public final class MapSplitter {

  private MapSplitter() {
    // static methods only
  }

  /**
   * Splits a map in two parts.
   * @param map Map to split
   * @param matcher Matcher function which is called on each (non-structural) entry to decide whether to put it in the
   *          matching map or unmatching map.
   * @return Result with the first map (matching) with all matching values, and the second map (unmatching) with all
   *         values that do not match.
   */
  @SuppressWarnings("unchecked")
  public static @NotNull SplitResult splitMap(Map<String, Object> map,
      @NotNull Function<Map.Entry<String, Object>, Boolean> matcher) {
    Map<String, Object> matching = new HashMap<>();
    Map<String, Object> unmatching = new HashMap<>();

    for (Map.Entry<String, Object> entry : map.entrySet()) {

      if (entry.getValue() instanceof Map) {
        // process nested maps
        processMapValue(entry, matching, unmatching, matcher);
        continue;
      }

      else if (entry.getValue() instanceof List) {
        if (listHasSubStructures((List<Object>)entry.getValue())) {
          // process nested "structural" lists
          processListValue(entry, matching, unmatching, matcher);
          continue;
        }
      }

      // value is not a structural (map/list) value - apply matching
      processSimpleValue(entry, matching, unmatching, matcher);
    }

    return new SplitResult(matching, unmatching);
  }

  private static void processSimpleValue(@NotNull Map.Entry<String, Object> entry,
      @NotNull Map<String, Object> matching,
      @NotNull Map<String, Object> unmatching,
      @NotNull Function<Map.Entry<String, Object>, Boolean> matcher) {

    if (matcher.apply(entry)) {
      matching.put(entry.getKey(), entry.getValue());
    }
    else {
      unmatching.put(entry.getKey(), entry.getValue());
    }
  }

  @SuppressWarnings("unchecked")
  private static void processMapValue(@NotNull Map.Entry<String, Object> entry,
      @NotNull Map<String, Object> matching,
      @NotNull Map<String, Object> unmatching,
      @NotNull Function<Map.Entry<String, Object>, Boolean> matcher) {

    Map<String, Object> map = (Map<String, Object>)entry.getValue();
    SplitResult subResult = splitMap(map, matcher);
    if (!subResult.getMatching().isEmpty()) {
      matching.put(entry.getKey(), subResult.getMatching());
    }
    if (!subResult.getUnmatching().isEmpty()) {
      unmatching.put(entry.getKey(), subResult.getUnmatching());
    }
  }

  @SuppressWarnings("unchecked")
  private static void processListValue(@NotNull Map.Entry<String, Object> entry,
      @NotNull Map<String, Object> matching,
      @NotNull Map<String, Object> unmatching,
      @NotNull Function<Map.Entry<String, Object>, Boolean> matcher) {

    // we cannot split up the list - so it's put to unmatched if at least one list entry is unmatched
    // to make processing easy we convert to list to a map and check of any unmatched
    List<Object> list = (List<Object>)entry.getValue();
    Map<String, Object> listMap = new HashMap<>();
    for (int i = 0; i < list.size(); i++) {
      listMap.put(Integer.toString(i), list.get(i));
    }
    SplitResult result = splitMap(listMap, matcher);
    if (result.getUnmatching().isEmpty()) {
      matching.put(entry.getKey(), entry.getValue());
    }
    else {
      unmatching.put(entry.getKey(), entry.getValue());
    }
  }

  private static boolean listHasSubStructures(@NotNull List<Object> list) {
    return list.stream()
        .filter(item -> (item instanceof List) || (item instanceof Map))
        .findFirst().isPresent();
  }

  /**
   * Result of {@link #splitMap(Map, Function)} method.
   */
  public static final class SplitResult {

    private final Map<String, Object> matching;
    private final Map<String, Object> unmatching;

    private SplitResult(@NotNull Map<String, Object> matching, @NotNull Map<String, Object> unmatching) {
      this.matching = matching;
      this.unmatching = unmatching;
    }

    public @NotNull Map<String, Object> getMatching() {
      return this.matching;
    }

    public @NotNull Map<String, Object> getUnmatching() {
      return this.unmatching;
    }

  }

}
