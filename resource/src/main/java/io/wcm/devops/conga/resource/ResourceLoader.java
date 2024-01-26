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

import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Resource loader to read resource and resource collections from filesystem or classpath.
 */
public final class ResourceLoader {

  /**
   * Path prefix for explicitly referencing a file.
   */
  public static final String FILE_PREFIX = "file:";

  /**
   * Path prefix for explicitly referencing a classpath resource.
   */
  public static final String CLASSPATH_PREFIX = "classpath:";

  private final ClassLoader classLoader;

  /**
   * Constructor.
   */
  public ResourceLoader() {
    this(ResourceLoader.class.getClassLoader());
  }

  /**
   * @param classLoader Class loader
   */
  public ResourceLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  /**
   * Get resource from filesystem (preferred) or classpath.
   * @param path Path
   * @return Resource or null if not found
   */
  public Resource getResource(String path) {
    return getResource(path, Resource.class);
  }

  /**
   * Get resource from filesystem (preferred) or classpath.
   * @param dir Parent directory
   * @param path Resource path relative to parent directory
   * @return Resource or null if not found
   */
  public Resource getResource(ResourceCollection dir, String path) {
    return getResource(getPrefix(dir) + dir.getPath() + "/" + path);
  }

  /**
   * Get resource collection from filesystem (preferred) or classpath.
   * @param path Path
   * @return Resource or null if not found
   */
  public ResourceCollection getResourceCollection(String path) {
    return getResource(path, ResourceCollection.class);
  }

  /**
   * Get resource collection from filesystem (preferred) or classpath.
   * @param dir Parent directory
   * @param path Resource path relative to parent directory
   * @return Resource or null if not found
   */
  public ResourceCollection getResourceCollection(ResourceCollection dir, String path) {
    return getResource(getPrefix(dir) + dir.getPath() + "/" + path, ResourceCollection.class);
  }

  /**
   * Try to get existing resource with any supported resource type. If none exists return the first type.
   * @param path Path
   * @param resourceClass Resource class
   * @return Resource instance
   */
  private <T extends ResourceInfo> T getResource(String path, Class<T> resourceClass) {
    List<ResourceType> resourceTypes = getSupportedResourceTypes(path);
    T firstResource = null;
    for (ResourceType resourceType : resourceTypes) {
      T resource = resourceType.create(removePrefix(path), resourceClass, this);
      if (resource.exists()) {
        return resource;
      }
      if (firstResource == null) {
        firstResource = resource;
      }
    }
    return firstResource;
  }

  /**
   * If an explicit resource type is declared via prefix return only this. Otherwise return all.
   * @param path Path
   * @return Supported resource types
   */
  private List<ResourceType> getSupportedResourceTypes(String path) {

    // check for explicit path specification
    for (ResourceType resourceType : ResourceType.values()) {
      if (StringUtils.startsWith(path, resourceType.getPrefix())) {
        return List.of(resourceType);
      }
    }

    // otherwise check all resource types in order of definition in enum
    return List.of(ResourceType.values());
  }

  ClassLoader getClassLoader() {
    return classLoader;
  }

  /**
   * Removes resource type prefix if a prefix is given.
   * @param path Path
   * @return Path without prefix
   */
  private static String removePrefix(String path) {
    for (ResourceType resourceType : ResourceType.values()) {
      if (StringUtils.startsWith(path, resourceType.getPrefix())) {
        return StringUtils.substringAfter(path, resourceType.getPrefix());
      }
    }
    return path;
  }

  private static String getPrefix(ResourceInfo resource) {
    for (ResourceType resourceType : ResourceType.values()) {
      if (resourceType.is(resource)) {
        return resourceType.getPrefix();
      }
    }
    throw new IllegalArgumentException("Unknown resource implementation: " + resource.getClass());
  }

}
