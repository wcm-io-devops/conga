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
package io.wcm.devops.conga.resource;

import java.io.File;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSortedSet;

class FileResourceCollectionImpl extends FileResourceImpl implements ResourceCollection {

  private final ResourceLoader resourceLoader;

  public FileResourceCollectionImpl(String path, ResourceLoader resourceLoader) {
    super(path);
    this.resourceLoader = resourceLoader;
  }

  public FileResourceCollectionImpl(File file, ResourceLoader resourceLoader) {
    super(file);
    this.resourceLoader = resourceLoader;
  }

  @Override
  protected void validateFile() {
    if (file.exists() && !this.file.isDirectory()) {
      throw new IllegalArgumentException("File is not a directory but a file: " + file.getPath());
    }
  }

  @Override
  public Resource getResource(String path) {
    return resourceLoader.getResource(this, path);
  }

  @Override
  public ResourceCollection getResourceCollection(String path) {
    return resourceLoader.getResourceCollection(this, path);
  }

  @Override
  public SortedSet<Resource> getResources() {
    if (!exists()) {
      return ImmutableSortedSet.of();
    }
    return ImmutableSortedSet.copyOf(Arrays.stream(file.listFiles())
        .filter(child -> child.isFile())
        .map(child -> new FileResourceImpl(child))
        .collect(Collectors.toList()));
  }

  @Override
  public SortedSet<ResourceCollection> getResourceCollections() {
    if (!exists()) {
      return ImmutableSortedSet.of();
    }
    return ImmutableSortedSet.copyOf(Arrays.stream(file.listFiles())
        .filter(child -> child.isDirectory())
        .map(child -> new FileResourceCollectionImpl(child, resourceLoader))
        .collect(Collectors.toList()));
  }

}
