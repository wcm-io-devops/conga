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

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

class MapExpanderTest {

  @Test
  void testNull() {
    assertEquals(null, expand(null));
  }

  @Test
  void testEmpty() {
    assertEquals(ImmutableMap.of(), expand(ImmutableMap.of()));
  }

  @Test
  void testSimple() {
    assertEquals(ImmutableMap.of("key1", "value1"),
        expand(ImmutableMap.of("key1", "value1")));

    assertEquals(ImmutableMap.of("key1", "value1", "key2", 5),
        expand(ImmutableMap.of("key1", "value1", "key2", 5)));
  }

  @Test
  void testSimpleNested() {
    assertEquals(ImmutableMap.of("key1", "value1", "key2", ImmutableMap.of("key21", "value21")),
        expand(ImmutableMap.of("key1", "value1", "key2", ImmutableMap.of("key21", "value21"))));
  }

  @Test
  void testOneLevel_1() {
    assertEquals(ImmutableMap.of("key1", "value1", "key2", ImmutableMap.of("key21", "value21")),
        expand(ImmutableMap.of("key1", "value1", "key2.key21", "value21")));
  }

  @Test
  void testOneLevel_2() {
    assertEquals(ImmutableMap.of("key1", "value1", "key2", ImmutableMap.of("key21", "value21", "key22", 55)),
        expand(ImmutableMap.of("key1", "value1", "key2.key21", "value21", "key2.key22", 55)));
  }

  @Test
  void testThreeLevels() {
    assertEquals(ImmutableMap.of("key1", "value1", "a", ImmutableMap.of("b", ImmutableMap.of(
        "c", ImmutableMap.of("1", "v1", "2", 99),
        "d", ImmutableMap.of("1", true)))),
        expand(ImmutableMap.of("key1", "value1", "a.b.c.1", "v1", "a.b.c.2", 99, "a.b.d.1", true)));
  }

  @Test
  void testMapDeeperLevel() {
    assertEquals(ImmutableMap.of("key1", ImmutableMap.of("a", ImmutableMap.of("b", ImmutableMap.of("c", "v1")))),
        expand(ImmutableMap.of("key1", ImmutableMap.of("a.b.c", "v1"))));
  }

  @Test
  void testWithList() {
    assertEquals(ImmutableMap.of("key1", ImmutableList.of(
        ImmutableMap.of("a", ImmutableMap.of("b", "v1", "c", ImmutableMap.of("d", "v2"))),
        ImmutableMap.of("a", ImmutableMap.of("b", "v3"))
        )),
        expand(ImmutableMap.of("key1", ImmutableList.of(
            ImmutableMap.of("a.b", "v1", "a.c.d", "v2"),
            ImmutableMap.of("a.b", "v3")
            ))));
  }

  @Test
  void testGetDeep() {
    assertNull(getDeep(ImmutableMap.of(), null));
    assertNull(getDeep(ImmutableMap.of(), "p1"));
    assertNull(getDeep(ImmutableMap.of(), "p1.p2"));
    assertNull(getDeep(ImmutableMap.of(), "p1.p2.p3"));

    assertEquals("v1", getDeep(ImmutableMap.of("p1", "v1", "p2", "v2"), "p1"));
    assertNull(getDeep(ImmutableMap.of("p1", "v1", "p2", "v2"), "p1.p2"));
    assertEquals("v1", getDeep(ImmutableMap.of("p1", ImmutableMap.of("p2", "v1"), "p2", "v2"), "p1.p2"));
    assertEquals("v1", getDeep(ImmutableMap.of("p1", ImmutableMap.of("p2", ImmutableMap.of("p3", "v1")), "p2", "v2"), "p1.p2.p3"));
  }

}
