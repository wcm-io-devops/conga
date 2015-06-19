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

/**
 * Abstraction for a list of resources from filesystem or classpath.
 */
public interface ResourceCollection extends ResourceInfo {

  /**
   * Get child resource.
   * @param path Resource path relative to resource collection
   * @return Resource or null if not found
   */
  Resource getResource(String path);

  /**
   * Get child resource collection.
   * @param path Resource path relative to resource collection
   * @return Resource collection or null if not found
   */
  ResourceCollection getResourceCollection(String path);

  /**
   * @return Resources in this collections.
   */
  List<Resource> getResources();

  /**
   * @return Resource collections in this collection.
   */
  List<ResourceCollection> getResourceCollections();

}
