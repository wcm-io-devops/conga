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

import static io.wcm.devops.conga.model.shared.MapMerger.merge;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class MapMergerTest {

  @Test
  public void testEmpty() {
    assertEquals(ImmutableMap.of(), merge(null, null));
    assertEquals(ImmutableMap.of(), merge(ImmutableMap.of(), null));
    assertEquals(ImmutableMap.of(), merge(null, ImmutableMap.of()));
    assertEquals(ImmutableMap.of(), merge(ImmutableMap.of(), ImmutableMap.of()));
  }

  @Test
  public void testSimple() {
    assertEquals(ImmutableMap.of("k1", "v1"), merge(ImmutableMap.of("k1", "v1"), null));
    assertEquals(ImmutableMap.of("k1", "v1"), merge(null, ImmutableMap.of("k1", "v1")));
    assertEquals(ImmutableMap.of("k1", "v1"), merge(ImmutableMap.of("k1", "v1"), ImmutableMap.of()));
    assertEquals(ImmutableMap.of("k1", "v1"), merge(ImmutableMap.of(), ImmutableMap.of("k1", "v1")));
  }

  @Test
  public void testMerge() {
    assertEquals(ImmutableMap.of("k1", "v1", "k2", "v2"),
        merge(ImmutableMap.of("k1", "v1"), ImmutableMap.of("k2", "v2")));
  }

  @Test
  public void testMergeOverride() {
    assertEquals(ImmutableMap.of("k1", "v1"),
        merge(ImmutableMap.of("k1", "v1"), ImmutableMap.of("k1", "v2")));
  }

  @Test
  public void testMergeDeep2() {
    assertEquals(ImmutableMap.of("k1", ImmutableMap.of("k11", "v11", "k12", "v12")),
        merge(ImmutableMap.of("k1", ImmutableMap.of("k11", "v11")), ImmutableMap.of("k1", ImmutableMap.of("k12", "v12"))));
  }

  @Test
  public void testMergeDeep3() {
    assertEquals(ImmutableMap.of("k1", ImmutableMap.of("k11", "v11", "k12", ImmutableMap.of("k111", "v111", "k112", "v112"))),
        merge(ImmutableMap.of("k1", ImmutableMap.of("k11", "v11", "k12", ImmutableMap.of("k111", "v111"))),
            ImmutableMap.of("k1", ImmutableMap.of("k12", ImmutableMap.of("k112", "v112")))));
  }

  @Test
  public void testMergeList() {
    assertEquals(ImmutableMap.of("k1", ImmutableList.of(ImmutableMap.of("k11", "v11"), "v12", ImmutableMap.of("k11", "v12", "k21", "v21"), "v13")),
        merge(ImmutableMap.of("k1", ImmutableList.of(ImmutableMap.of("k11", "v11"), "v12")),
            ImmutableMap.of("k1", ImmutableList.of(ImmutableMap.of("k11", "v12", "k21", "v21"), "v13"))));
  }

}
