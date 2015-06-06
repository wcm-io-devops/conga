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
package io.wcm.devops.conga.model.shared;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class MapExpanderTest {

  @Test
  public void testNull() {
    assertEquals(null, MapExpander.expand(null));
  }

  @Test
  public void testEmpty() {
    assertEquals(ImmutableMap.of(), MapExpander.expand(ImmutableMap.of()));
  }

  @Test
  public void testSimple() {
    assertEquals(ImmutableMap.of("key1", "value1"),
        MapExpander.expand(ImmutableMap.of("key1", "value1")));

    assertEquals(ImmutableMap.of("key1", "value1", "key2", 5),
        MapExpander.expand(ImmutableMap.of("key1", "value1", "key2", 5)));
  }

  @Test
  public void testSimpleNested() {
    assertEquals(ImmutableMap.of("key1", "value1", "key2", ImmutableMap.of("key21", "value21")),
        MapExpander.expand(ImmutableMap.of("key1", "value1", "key2", ImmutableMap.of("key21", "value21"))));
  }

  @Test
  public void testExpandOneLevel() {
    assertEquals(ImmutableMap.of("key1", "value1", "key2", ImmutableMap.of("key21", "value21")),
        MapExpander.expand(ImmutableMap.of("key1", "value1", "key2.key21", "value21")));

    assertEquals(ImmutableMap.of("key1", "value1", "key2", ImmutableMap.of("key21", "value21", "key22", 55)),
        MapExpander.expand(ImmutableMap.of("key1", "value1", "key2.key21", "value21", "key2.key22", 55)));
  }

  // FIXME: does not work yet, add more unit tests
  @Test
  @Ignore
  public void testExpandThreeLevels() {
    assertEquals(ImmutableMap.of("key1", "value1", "a", ImmutableMap.of("b", ImmutableMap.of(
        "c", ImmutableMap.of("1", "v1", "2", "v2")),
        "d", ImmutableMap.of("1", true))),
        MapExpander.expand(ImmutableMap.of("key1", "value1", "a.b.c.1", "v1", "a.b.c.2", 99, "a.b.d.1", true)));
  }

}
