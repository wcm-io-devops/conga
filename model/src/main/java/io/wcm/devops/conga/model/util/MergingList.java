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

import java.util.LinkedList;

/**
 * Special list that marks a list as "mergeable" in downstream and preservers the merge position.
 * @param <T> List type
 */
@SuppressWarnings("java:S2160") // equals/hashCode is implemented in base class
final class MergingList<T> extends LinkedList<T> {
  private static final long serialVersionUID = 1L;

  private int mergePositionIndex;

  MergingList() {
    this.mergePositionIndex = -1;
  }

  MergingList(MergingList<T> mergingList) {
    mergingList.forEach(super::add);
    this.mergePositionIndex = mergingList.mergePositionIndex;
  }

  /**
   * Add item to list. If the item is a _merge_ token it is not added, but the merge position is recorded.
   * Duplicate items are ignored.
   * @param item Item
   */
  void addCheckMergeToken(T item) {
    if (MapMerger.LIST_MERGE_ENTRY.equals(item)) {
      if (mergePositionIndex < 0) {
        mergePositionIndex = this.size();
      }
    }
    else {
      this.addIgnoreDuplicates(item);
    }
  }

  /**
   * Adds a new item at the merge position (or at the end if not merge position was recorded).
   * Duplicate items are ignored.
   * @param item item
   */
  @Override
  public boolean add(T item) {
    if (MapMerger.LIST_MERGE_ENTRY.equals(item)) {
      return false;
    }
    if (mergePositionIndex >= 0) {
      if (this.addIgnoreDuplicates(mergePositionIndex, item)) {
        mergePositionIndex++;
      }
      return false;
    }
    else {
      return this.addIgnoreDuplicates(item);
    }
  }

  private boolean addIgnoreDuplicates(T item) {
    if (!this.contains(item)) {
      super.add(item);
      return true;
    }
    return false;
  }

  private boolean addIgnoreDuplicates(int index, T item) {
    if (!this.contains(item)) {
      if (index > this.size() - 1) {
        super.add(item);
      }
      else {
        super.add(index, item);
      }
      return true;
    }
    return false;
  }

  /**
   * @return true if list has a merge position
   */
  public boolean hasMergePosition() {
    return mergePositionIndex >= 0;
  }

}
