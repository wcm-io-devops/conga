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
package io.wcm.devops.conga.generator.spi.context;

import java.util.List;

/**
 * Context for {@link io.wcm.devops.conga.generator.spi.FileHeaderPlugin}.
 */
public final class FileHeaderContext extends AbstractPluginContext<FileHeaderContext> {

  private String message;
  private String timestamp;
  private List<String> versions;

  /**
   * @return File header message
   */
  public String getMessage() {
    return this.message;
  }

  /**
   * @param value File header message
   * @return this
   */
  public FileHeaderContext message(String value) {
    message = value;
    return this;
  }

  /**
   * @return Formatted generation timestampe
   */
  public String getTimestamp() {
    return this.timestamp;
  }

  /**
   * @param value Formatted generation timestampe
   * @return this
   */
  public FileHeaderContext timestamp(String value) {
    timestamp = value;
    return this;
  }

  /**
   * @return Role/Template definition version(s)
   */
  public List<String> getVersions() {
    return this.versions;
  }

  /**
   * @param value Role/Template definition version(s)
   * @return this
   */
  public FileHeaderContext versions(List<String> value) {
    versions = value;
    return this;
  }

}
