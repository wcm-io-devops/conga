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
import java.util.Map;

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
    // Check for key-based duplicate in case of Map objects with "key" field
    int existingIndex = findIndexByKey(item);
    if (existingIndex >= 0) {
      // Item with same key already exists - merge the maps
      mergeItemAtIndex(existingIndex, item);
      return false;
    }
    // Standard duplicate check
    if (!this.contains(item)) {
      super.add(item);
      return true;
    }
    return false;
  }

  private boolean addIgnoreDuplicates(int index, T item) {
    // Check for key-based duplicate in case of Map objects with "key" field
    int existingIndex = findIndexByKey(item);
    if (existingIndex >= 0) {
      // Item with same key already exists - merge the maps
      mergeItemAtIndex(existingIndex, item);
      return false;
    }
    // Standard duplicate check
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
   * Merges a new item into an existing item at the given index.
   * For Map objects, performs a deep merge where the existing item takes precedence.
   * This is because the existing item is typically from a variant (higher priority),
   * and the new item is from a base config (lower priority).
   * @param index Index of existing item
   * @param newItem New item to merge in
   */
  @SuppressWarnings("unchecked")
  private void mergeItemAtIndex(int index, T newItem) {
    T existingItem = this.get(index);
    if (existingItem instanceof Map && newItem instanceof Map) {
      Map<Object, Object> existingMap = (Map<Object, Object>)existingItem;
      Map<?, ?> newMap = (Map<?, ?>)newItem;
      
      // Merge: Start with new (base), then override with existing (variant)
      // This preserves fields from base that are not in variant
      Map<Object, Object> mergedMap = new java.util.HashMap<>(newMap);
      mergedMap.putAll(existingMap);
      
      super.set(index, (T)mergedMap);
    }
    // For non-Map objects, keep the existing one
    // (do nothing, existing item has higher priority)
  }

  /**
   * Finds an existing item in the list that has the same "key" value as the given item.
   * This applies only to Map objects that contain a "key" field.
   * @param item Item to check
   * @return Index of existing item with same key, or -1 if not found
   */
  @SuppressWarnings("unchecked")
  private int findIndexByKey(T item) {
    if (!(item instanceof Map)) {
      return -1;
    }
    Map<?, ?> itemMap = (Map<?, ?>)item;
    Object itemKey = itemMap.get("key");
    if (itemKey == null) {
      return -1;
    }
    
    for (int i = 0; i < this.size(); i++) {
      T existing = this.get(i);
      if (existing instanceof Map) {
        Map<?, ?> existingMap = (Map<?, ?>)existing;
        Object existingKey = existingMap.get("key");
        if (itemKey.equals(existingKey)) {
          return i;
        }
      }
    }
    return -1;
  }

  /**
   * @return true if list has a merge position
   */
  public boolean hasMergePosition() {
    return mergePositionIndex >= 0;
  }

}
