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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

abstract class AbstractClasspathResourceImpl implements ResourceInfo {

  protected final String path;
  protected final ClassLoader classLoader;

  public AbstractClasspathResourceImpl(String path, ClassLoader classLoader) {
    this.path = path;
    this.classLoader = classLoader;
  }

  @Override
  public final String getName() {
    return FilenameUtils.getName(path);
  }

  @Override
  public final String getPath() {
    return path;
  }

  @Override
  public final String getCanonicalPath() {
    return ResourceLoader.CLASSPATH_PREFIX + path;
  }

  protected static String convertPath(String path) {
    return StringUtils.replace(StringUtils.removeStart(path, "/"), "\\", "/");
  }

  @Override
  public String toString() {
    return getPath();
  }

}
