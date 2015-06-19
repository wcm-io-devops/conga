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

/**
 * Error when accessing resources.
 */
public final class ResourceException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  /**
   * @param message Message
   * @param cause Cause
   */
  public ResourceException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message Message
   */
  public ResourceException(String message) {
    super(message);
  }

}
