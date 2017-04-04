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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

class FileResourceImpl extends AbstractFileResourceInfoImpl implements Resource {

  FileResourceImpl(String path) {
    super(path);
  }

  FileResourceImpl(File file) {
    super(file);
  }

  @Override
  protected void validateFile() {
    if (file.exists() && !this.file.isFile()) {
      throw new IllegalArgumentException("File is not a file but a directory: " + file.getPath());
    }
  }

  @Override
  public final long getLastModified() {
    return file.lastModified();
  }

  @Override
  public InputStream getInputStream() {
    try {
      return new BufferedInputStream(new FileInputStream(file));
    }
    catch (FileNotFoundException ex) {
      throw new ResourceException("File does not exist: " + getCanonicalPath());
    }
  }

}
