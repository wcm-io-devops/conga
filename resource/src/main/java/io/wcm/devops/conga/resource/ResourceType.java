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

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Supported resource implementations.
 */
enum ResourceType {

  FILE(ResourceLoader.FILE_PREFIX,
      (path, resourceLoader) -> new FileResourceImpl(path),
      (path, resourceLoader) -> new FileResourceCollectionImpl(path, resourceLoader),
      resource -> resource instanceof AbstractFileResourceInfoImpl),

      CLASSPATH(ResourceLoader.CLASSPATH_PREFIX,
          (path, resourceLoader) -> new ClasspathResourceImpl(path, resourceLoader),
          (path, resourceLoader) -> new ClasspathResourceCollectionImpl(path, resourceLoader),
          resource -> resource instanceof AbstractClasspathResourceImpl);

  private final String prefix;
  private final BiFunction<String, ResourceLoader, Resource> resourceFactory;
  private final BiFunction<String, ResourceLoader, ResourceCollection> resourceCollectionFactory;
  private final Function<ResourceInfo, Boolean> resourceMatcher;

  private ResourceType(String prefix,
      BiFunction<String, ResourceLoader, Resource> resourceFactory,
      BiFunction<String, ResourceLoader, ResourceCollection> resourceCollectionFactory,
      Function<ResourceInfo, Boolean> resourceMatcher) {
    this.prefix = prefix;
    this.resourceFactory = resourceFactory;
    this.resourceCollectionFactory = resourceCollectionFactory;
    this.resourceMatcher = resourceMatcher;
  }

  public String getPrefix() {
    return prefix;
  }

  @SuppressWarnings("unchecked")
  public <T extends ResourceInfo> T create(String path, Class<T> resourceClass, ResourceLoader resourceLoader) {
    if (resourceClass == Resource.class) {
      return (T)resourceFactory.apply(path, resourceLoader);
    }
    if (resourceClass == ResourceCollection.class) {
      return (T)resourceCollectionFactory.apply(path, resourceLoader);
    }
    throw new IllegalArgumentException("Class not supported: " + resourceClass.getName());
  }

  public boolean is(ResourceInfo resource) {
    return resourceMatcher.apply(resource);
  }

}
