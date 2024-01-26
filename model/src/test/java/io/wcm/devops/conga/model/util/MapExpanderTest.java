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

import static io.wcm.devops.conga.model.util.MapExpander.expand;
import static io.wcm.devops.conga.model.util.MapExpander.getDeep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class MapExpanderTest {

  @Test
  void testNull() {
    assertEquals(null, expand(null));
  }

  @Test
  void testEmpty() {
    assertEquals(Map.of(), expand(Map.of()));
  }

  @Test
  void testSimple() {
    assertEquals(Map.of("key1", "value1"),
        expand(Map.of("key1", "value1")));

    assertEquals(Map.of("key1", "value1", "key2", 5),
        expand(Map.of("key1", "value1", "key2", 5)));
  }

  @Test
  void testSimpleNested() {
    assertEquals(Map.of("key1", "value1", "key2", Map.of("key21", "value21")),
        expand(Map.of("key1", "value1", "key2", Map.of("key21", "value21"))));
  }

  @Test
  void testOneLevel_1() {
    assertEquals(Map.of("key1", "value1", "key2", Map.of("key21", "value21")),
        expand(Map.of("key1", "value1", "key2.key21", "value21")));
  }

  @Test
  void testOneLevel_2() {
    assertEquals(Map.of("key1", "value1", "key2", Map.of("key21", "value21", "key22", 55)),
        expand(Map.of("key1", "value1", "key2.key21", "value21", "key2.key22", 55)));
  }

  @Test
  void testThreeLevels() {
    assertEquals(Map.of("key1", "value1", "a", Map.of("b", Map.of(
        "c", Map.of("1", "v1", "2", 99),
        "d", Map.of("1", true)))),
        expand(Map.of("key1", "value1", "a.b.c.1", "v1", "a.b.c.2", 99, "a.b.d.1", true)));
  }

  @Test
  void testMapDeeperLevel() {
    assertEquals(Map.of("key1", Map.of("a", Map.of("b", Map.of("c", "v1")))),
        expand(Map.of("key1", Map.of("a.b.c", "v1"))));
  }

  @Test
  void testWithList() {
    assertEquals(Map.of("key1", List.of(
        Map.of("a", Map.of("b", "v1", "c", Map.of("d", "v2"))),
        Map.of("a", Map.of("b", "v3"))
        )),
        expand(Map.of("key1", List.of(
            Map.of("a.b", "v1", "a.c.d", "v2"),
            Map.of("a.b", "v3")
            ))));
  }

  @Test
  void testGetDeep() {
    assertNull(getDeep(Map.of(), ""));
    assertNull(getDeep(Map.of(), "p1"));
    assertNull(getDeep(Map.of(), "p1.p2"));
    assertNull(getDeep(Map.of(), "p1.p2.p3"));

    assertEquals("v1", getDeep(Map.of("p1", "v1", "p2", "v2"), "p1"));
    assertNull(getDeep(Map.of("p1", "v1", "p2", "v2"), "p1.p2"));
    assertEquals("v1", getDeep(Map.of("p1", Map.of("p2", "v1"), "p2", "v2"), "p1.p2"));
    assertEquals("v1", getDeep(Map.of("p1", Map.of("p2", Map.of("p3", "v1")), "p2", "v2"), "p1.p2.p3"));
  }

}
