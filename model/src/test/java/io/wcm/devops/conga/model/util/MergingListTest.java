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

import static io.wcm.devops.conga.model.util.MapMerger.LIST_MERGE_ENTRY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

class MergingListTest {

  private MergingList<String> underTest;

  @BeforeEach
  void setUp() {
    underTest = new MergingList<>();
  }

  @Test
  void testNoMerging_Duplicates() {
    underTest.addCheckMergeToken("item1");
    underTest.addCheckMergeToken("item2");
    underTest.addCheckMergeToken("item2");

    underTest.add("item4");
    underTest.add("item5");
    underTest.add("item5");

    assertEquals(ImmutableList.of("item1", "item2", "item4", "item5"), underTest);
  }

  @Test
  void testMergeStart() {
    underTest.addCheckMergeToken(LIST_MERGE_ENTRY);
    underTest.addCheckMergeToken("item1");
    underTest.addCheckMergeToken("item2");

    underTest.add("item4");
    underTest.add("item5");

    assertEquals(ImmutableList.of("item4", "item5", "item1", "item2"), underTest);
  }

  @Test
  void testMergeMiddle() {
    underTest.addCheckMergeToken("item1");
    underTest.addCheckMergeToken(LIST_MERGE_ENTRY);
    underTest.addCheckMergeToken("item2");

    underTest.add("item4");
    underTest.add("item5");

    assertEquals(ImmutableList.of("item1", "item4", "item5", "item2"), underTest);
  }

  @Test
  void testMergeMiddle_Duplicates() {
    underTest.addCheckMergeToken("item1");
    underTest.addCheckMergeToken(LIST_MERGE_ENTRY);
    underTest.addCheckMergeToken("item2");

    underTest.add("item2");
    underTest.add("item4");
    underTest.add("item1");
    underTest.add("item5");

    assertEquals(ImmutableList.of("item1", "item4", "item5", "item2"), underTest);
  }

  @Test
  void testMergeEnd() {
    underTest.addCheckMergeToken("item1");
    underTest.addCheckMergeToken("item2");
    underTest.addCheckMergeToken(LIST_MERGE_ENTRY);

    underTest.add("item4");
    underTest.add("item5");

    assertEquals(ImmutableList.of("item1", "item2", "item4", "item5"), underTest);
  }

  @Test
  void testMergeMultipleTokens() {
    underTest.addCheckMergeToken("item1");
    underTest.addCheckMergeToken(LIST_MERGE_ENTRY);
    underTest.addCheckMergeToken("item2");
    underTest.addCheckMergeToken(LIST_MERGE_ENTRY);

    underTest.add("item4");
    underTest.add("item5");

    assertEquals(ImmutableList.of("item1", "item4", "item5", "item2"), underTest);
  }

}
