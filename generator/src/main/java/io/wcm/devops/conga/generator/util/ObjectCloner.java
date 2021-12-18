/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2021 wcm.io
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
package io.wcm.devops.conga.generator.util;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;

/**
 * Clone maps.
 */
public final class ObjectCloner {

  private ObjectCloner() {
    // static methods only
  }

  /**
   * Deep clones an object.
   * @param <T> Type is expected to be serializable (but not enforced here to also support e.g. Map interface variables)
   * @param input Input object
   * @return Cloned object
   */
  @SuppressWarnings("unchecked")
  public static <T> T deepClone(T input) {
    if (!(input instanceof Serializable)) {
      throw new IllegalArgumentException("Input not serializable: " + input);
    }
    return (T)SerializationUtils.clone((Serializable)input);
  }

}
