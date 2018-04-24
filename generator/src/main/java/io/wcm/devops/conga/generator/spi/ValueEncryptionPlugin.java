/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2018 wcm.io
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
package io.wcm.devops.conga.generator.spi;

import io.wcm.devops.conga.generator.spi.context.ValueEncryptionContext;

/**
 * Encrypts a sensitive configuration parameter value.
 */
public interface ValueEncryptionPlugin extends Plugin {

  /**
   * When a value needs to be encrypted, the first enabled encryption plugin is used.
   * Via this method plugin implementations can report whether they are configured properly and can be used.
   * @return true if Encryption plugin is configured properly.
   */
  boolean isEnabled();

  /**
   * Encrypt value.
   * @param parameterName Parameter name
   * @param value Unencrypted value
   * @param context Context
   * @return Encrypted value.
   * @throws UnsupportedOperationException when the plugin is not enabled.
   */
  Object encrypt(String parameterName, Object value, ValueEncryptionContext context);

}
