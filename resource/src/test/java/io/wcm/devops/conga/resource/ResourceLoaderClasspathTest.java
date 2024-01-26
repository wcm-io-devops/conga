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

import static io.wcm.devops.conga.resource.ResourceLoader.CLASSPATH_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResourceLoaderClasspathTest {

  private static final String ROOT = "/test-files";

  private ResourceLoader underTest;

  @BeforeEach
  void setUp() {
    underTest = new ResourceLoader();
  }

  @Test
  void testResource() throws Exception {
    Resource resource = underTest.getResource(CLASSPATH_PREFIX + ROOT + "/folder1/file1.txt");

    assertTrue(resource.exists());
    assertEquals("file1.txt", resource.getName());
    assertEquals("txt", resource.getFileExtension());
    assertEquals(ROOT + "/folder1/file1.txt", resource.getPath());
    assertEquals(CLASSPATH_PREFIX + ROOT + "/folder1/file1.txt", resource.getCanonicalPath());

    assertTrue(resource.getLastModified() > 0);

    try (InputStream is = resource.getInputStream()) {
      assertEquals("File 1", IOUtils.toString(is, StandardCharsets.UTF_8));
    }
  }

  @Test
  void testResourceCollection() throws Exception {
    ResourceCollection col = underTest.getResourceCollection(CLASSPATH_PREFIX + ROOT + "/folder1");

    assertTrue(col.exists());
    assertEquals("folder1", col.getName());
    assertEquals(ROOT + "/folder1", col.getPath());
    assertEquals(CLASSPATH_PREFIX + ROOT + "/folder1", col.getCanonicalPath());

    List<Resource> resources = List.copyOf(col.getResources());
    assertEquals(2, resources.size());
    assertEquals("file1.txt", resources.get(0).getName());
    assertEquals("file2.txt", resources.get(1).getName());

    List<ResourceCollection> resourceCollections = List.copyOf(col.getResourceCollections());
    assertEquals(1, resourceCollections.size());
    assertEquals("folder2", resourceCollections.get(0).getName());

    List<Resource> folder2Resources = List.copyOf(resourceCollections.get(0).getResources());
    assertEquals(1, folder2Resources.size());
    assertEquals("file3.txt", folder2Resources.get(0).getName());
  }

  @Test
  void testResourceAutoDetect() throws Exception {
    Resource resource = underTest.getResource(ROOT + "/folder1/file1.txt");

    assertTrue(resource.exists());
    assertEquals("file1.txt", resource.getName());
    assertEquals(ROOT + "/folder1/file1.txt", resource.getPath());
    assertEquals(CLASSPATH_PREFIX + ROOT + "/folder1/file1.txt", resource.getCanonicalPath());

    assertTrue(resource.getLastModified() > 0);

    try (InputStream is = resource.getInputStream()) {
      assertEquals("File 1", IOUtils.toString(is, StandardCharsets.UTF_8));
    }
  }

  @Test
  void testNonExistingResource() throws Exception {
    Resource resource = underTest.getResource(CLASSPATH_PREFIX + ROOT + "/folder1/invalid.txt");
    assertFalse(resource.exists());
    assertTrue(resource instanceof ClasspathResourceImpl);
  }

  @Test
  void testNonExistingResourceCollection() throws Exception {
    ResourceCollection col = underTest.getResourceCollection(CLASSPATH_PREFIX + ROOT + "/invalidFolder");
    assertFalse(col.exists());
    assertEquals(List.of(), List.copyOf(col.getResources()));
    assertEquals(List.of(), List.copyOf(col.getResourceCollections()));
  }

  @Test
  void testResourceByParentFolder() throws Exception {
    ResourceCollection col = underTest.getResourceCollection(CLASSPATH_PREFIX + ROOT + "/folder1");
    Resource resource = underTest.getResource(col, "folder2/file3.txt");
    assertTrue(resource.exists());
    assertEquals("file3.txt", resource.getName());
  }

  @Test
  void testResourceCollectionByParentFolder() throws Exception {
    ResourceCollection colParent = underTest.getResourceCollection(CLASSPATH_PREFIX + ROOT + "/folder1");
    ResourceCollection col = underTest.getResourceCollection(colParent, "folder2");
    assertTrue(col.exists());
    assertEquals("folder2", col.getName());
  }

  @Test
  void testResourceFromExternalJar() throws Exception {
    Resource resource = underTest.getResource(CLASSPATH_PREFIX + "/META-INF/maven/org.apache.commons/commons-lang3/pom.xml");
    assertTrue(resource.exists());
  }

  @Test
  void testResourceCollectionFromExternalJar() throws Exception {
    ResourceCollection col = underTest.getResourceCollection(CLASSPATH_PREFIX + "/META-INF/maven/org.apache.commons/commons-lang3");
    assertTrue(col.exists());
    assertEquals(2, col.getResources().size());
  }

}
