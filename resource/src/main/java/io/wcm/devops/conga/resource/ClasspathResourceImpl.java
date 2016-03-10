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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

class ClasspathResourceImpl extends AbstractClasspathResourceImpl implements Resource {

  private static final long CLASS_LOADING_TIME = new Date().getTime();

  private final URL url;

  ClasspathResourceImpl(String path, ResourceLoader resourceLoader) {
    super(path, resourceLoader.getClassLoader());
    this.url = this.classLoader.getResource(convertPath(path));
  }

  ClasspathResourceImpl(URL url, ResourceLoader resourceLoader) {
    super(url.getPath(), resourceLoader.getClassLoader());
    this.url = url;
  }

  @Override
  public boolean exists() {
    return (url != null);
  }

  @Override
  public final long getLastModified() {
    return CLASS_LOADING_TIME;
  }

  @Override
  public InputStream getInputStream() {
    try {
      return url.openStream();
    }
    catch (IOException ex) {
      throw new ResourceException("Unable to open classpath stream for: " + path, ex);
    }
  }

}
