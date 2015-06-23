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
package io.wcm.devops.conga.generator.context;

import io.wcm.devops.conga.generator.spi.context.PluginContext;
import io.wcm.devops.conga.generator.spi.context.PluginFileContext;

import java.io.File;
import java.util.Map;

/**
 * Implementation of {@link PluginContext} with file context.
 */
public abstract class AbstractPluginFileContextImpl<T> extends AbstractPluginContextImpl<T>
    implements PluginFileContext {

  private File file;
  private String charset;
  private Map<String, Object> options;

  @Override
  public File getFile() {
    return file;
  }

  /**
   * @param value File
   * @return this
   */
  @SuppressWarnings("unchecked")
  public T file(File value) {
    file = value;
    return (T)this;
  }

  @Override
  public String getCharset() {
    return charset;
  }

  /**
   * @param value Charset
   * @return this
   */
  @SuppressWarnings("unchecked")
  public T charset(String value) {
    charset = value;
    return (T)this;
  }

  @Override
  public Map<String, Object> getOptions() {
    return options;
  }

  /**
   * @param value Config
   * @return this
   */
  @SuppressWarnings("unchecked")
  public T options(Map<String, Object> value) {
    options = value;
    return (T)this;
  }

}
