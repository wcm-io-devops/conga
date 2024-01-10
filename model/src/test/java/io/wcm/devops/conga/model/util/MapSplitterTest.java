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

import static io.wcm.devops.conga.model.util.MapSplitter.splitMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import io.wcm.devops.conga.model.util.MapSplitter.SplitResult;

class MapSplitterTest {

  private static final Map<String, Object> TEST_MAP = Map.of(
      "var1", "value1",
      "var2", "value2",
      "obj1", Map.of(
          "var11", "value11",
          "var12", "value12",
          "obj13", Map.of(
              "var131", "value131",
              "var132", "value132"),
          "list14", List.of(
              "value14a",
              "value14b",
              "value14c")),
      "list2", List.of(
          Map.of(
              "var21a", "value11a",
              "var22a", "value22a",
              "obj23a", Map.of(
                  "var23a1", "value23a1",
                  "var23a2", "value23a2")),
          Map.of(
              "var21b", "value11b",
              "var22b", "value22b")));

  private SplitResult result;

  @Test
  void testAllIn() {
    result = splitMap(TEST_MAP, entry -> true);
    assertEquals(TEST_MAP, result.getMatching());
    assertTrue(result.getUnmatching().isEmpty());
  }

  @Test
  void testAllOut() {
    result = splitMap(TEST_MAP, entry -> false);
    assertTrue(result.getMatching().isEmpty());
    assertEquals(TEST_MAP, result.getUnmatching());
  }

  @Test
  void testKeyStartsWithVar1() {
    result = splitMap(TEST_MAP, entry -> StringUtils.startsWith(entry.getKey(), "var1"));
    assertEquals(Map.of(
        "var1", "value1",
        "obj1", Map.of(
            "var11", "value11",
            "var12", "value12",
            "obj13", Map.of(
                "var131", "value131",
                "var132", "value132"))),
        result.getMatching());
    assertEquals(Map.of(
        "var2", "value2",
        "obj1", Map.of(
            "list14", List.of(
                "value14a",
                "value14b",
                "value14c")),
        "list2", List.of(
            Map.of(
                "var21a", "value11a",
                "var22a", "value22a",
                "obj23a", Map.of(
                    "var23a1", "value23a1",
                    "var23a2", "value23a2")),
            Map.of(
                "var21b", "value11b",
                "var22b", "value22b"))),
        result.getUnmatching());
  }

  @Test
  void testValueStartsWithValue2() {
    result = splitMap(TEST_MAP, entry -> matchesSimpleListValue(entry.getValue(),
        value -> (value instanceof String) && StringUtils.startsWith(value.toString(), "value2")));
    assertEquals(Map.of(
        "var2", "value2"),
        result.getMatching());
    assertEquals(Map.of(
        "var1", "value1",
        "obj1", Map.of(
            "var11", "value11",
            "var12", "value12",
            "obj13", Map.of(
                "var131", "value131",
                "var132", "value132"),
            "list14", List.of(
                "value14a",
                "value14b",
                "value14c")),
        "list2", List.of(
            Map.of(
                "var21a", "value11a",
                "var22a", "value22a",
                "obj23a", Map.of(
                    "var23a1", "value23a1",
                    "var23a2", "value23a2")),
            Map.of(
                "var21b", "value11b",
                "var22b", "value22b"))),
        result.getUnmatching());
  }

  @Test
  void testStartsWithValue14() {
    result = splitMap(TEST_MAP, entry -> matchesSimpleListValue(entry.getValue(),
        value -> (value instanceof String) && StringUtils.startsWith(value.toString(), "value14")));
    assertEquals(Map.of(
        "obj1", Map.of(
            "list14", List.of(
                "value14a",
                "value14b",
                "value14c"))),
        result.getMatching());
    assertEquals(Map.of(
        "var1", "value1",
        "var2", "value2",
        "obj1", Map.of(
            "var11", "value11",
            "var12", "value12",
            "obj13", Map.of(
                "var131", "value131",
                "var132", "value132")),
        "list2", List.of(
            Map.of(
                "var21a", "value11a",
                "var22a", "value22a",
                "obj23a", Map.of(
                    "var23a1", "value23a1",
                    "var23a2", "value23a2")),
            Map.of(
                "var21b", "value11b",
                "var22b", "value22b"))),
        result.getUnmatching());
  }

  @AfterEach
  void validateWithMerge() {
    // all results should lead to the original map when merged back together
    assertEquals(TEST_MAP, MapMerger.merge(result.getMatching(), result.getUnmatching()));
  }

  private boolean matchesSimpleListValue(Object value, Function<Object, Boolean> matcher) {
    if (value instanceof List) {
      for (Object item : (List)value) {
        if (!matcher.apply(item)) {
          return false;
        }
      }
      return true;
    }
    else {
      return matcher.apply(value);
    }
  }

}
