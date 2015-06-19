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
import java.io.IOException;

abstract class AbstractFileResourceInfoImpl implements ResourceInfo {

  protected final File file;

  public AbstractFileResourceInfoImpl(String path) {
    this(new File(path));
  }

  public AbstractFileResourceInfoImpl(File file) {
    this.file = file;
    validateFile();
  }

  protected abstract void validateFile();

  @Override
  public final boolean exists() {
    return file.exists();
  }

  @Override
  public final String getName() {
    return file.getName();
  }

  @Override
  public final String getPath() {
    return file.getPath();
  }

  @Override
  public final String getCanonicalPath() {
    try {
      return file.getCanonicalPath();
    }
    catch (IOException ex) {
      throw new ResourceException("Unable to get canonical path from " + file.getPath(), ex);
    }
  }

  @Override
  public String toString() {
    return getPath();
  }

}
