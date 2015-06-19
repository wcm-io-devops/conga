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
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

class FileResourceCollectionImpl extends FileResourceImpl implements ResourceCollection {

  public FileResourceCollectionImpl(String path) {
    super(path);
  }

  public FileResourceCollectionImpl(File file) {
    super(file);
  }

  @Override
  protected void validateFile() {
    if (file.exists() && !this.file.isDirectory()) {
      throw new IllegalArgumentException("File is not a directory but a file: " + file.getPath());
    }
  }

  @Override
  public List<Resource> getResources() {
    if (!exists()) {
      return ImmutableList.of();
    }
    return ImmutableList.copyOf(Arrays.stream(file.listFiles())
        .filter(child -> child.isFile())
        .map(child -> new FileResourceImpl(child))
        .collect(Collectors.toList()));
  }

  @Override
  public List<ResourceCollection> getResourceCollections() {
    if (!exists()) {
      return ImmutableList.of();
    }
    return ImmutableList.copyOf(Arrays.stream(file.listFiles())
        .filter(child -> child.isDirectory())
        .map(child -> new FileResourceCollectionImpl(child))
        .collect(Collectors.toList()));
  }

}
