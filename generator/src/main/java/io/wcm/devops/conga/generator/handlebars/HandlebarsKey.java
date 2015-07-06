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
package io.wcm.devops.conga.generator.handlebars;

import java.util.Objects;

/**
 * Key for identifying handlebar instances per file extension and charset.
 */
final class HandlebarsKey {

  private final String escapingStrategy;
  private final String charset;
  private final String combinedKey;

  public HandlebarsKey(String escapingStrategy, String charset) {
    Objects.requireNonNull(escapingStrategy, "Escaping strategy is missing");
    Objects.requireNonNull(charset, "Charset is missing");
    this.escapingStrategy = escapingStrategy;
    this.charset = charset;
    this.combinedKey = escapingStrategy + "#" + charset;
  }

  public String getEscapingStrategy() {
    return this.escapingStrategy;
  }

  public String getCharset() {
    return this.charset;
  }

  @Override
  public int hashCode() {
    return combinedKey.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof HandlebarsKey) {
      return combinedKey.equals(((HandlebarsKey)obj).combinedKey);
    }
    return false;
  }

  @Override
  public String toString() {
    return "escapingStrategy=" + this.escapingStrategy + ", charset=" + this.charset;
  }

}
