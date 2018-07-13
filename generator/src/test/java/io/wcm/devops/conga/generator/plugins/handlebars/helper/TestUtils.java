/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2016 wcm.io
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
package io.wcm.devops.conga.generator.plugins.handlebars.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Options.Buffer;

import io.wcm.devops.conga.generator.spi.handlebars.HelperPlugin;
import io.wcm.devops.conga.generator.spi.handlebars.context.HelperContext;

final class TestUtils {

  private TestUtils() {
    // static methods only
  }

  public static void assertHelper(String expected, HelperPlugin<Object> helper, Object context, Options options) throws IOException {
    assertHelper(expected, helper, context, options, null);
  }

  public static void assertHelper(String expected, HelperPlugin<Object> helper, Object context, Options options,
      HelperContext pluginContext) throws IOException {
    Object result = executeHelper(helper, context, options, pluginContext);
    assertEquals(expected, result);
  }

  public static Object executeHelper(HelperPlugin<Object> helper, Object context, Options options) throws IOException {
    return executeHelper(helper, context, options, null);
  }

  public static Object executeHelper(HelperPlugin<Object> helper, Object context, Options options,
      HelperContext pluginContext) throws IOException {
    Object result = helper.apply(context, options, pluginContext);
    if (result instanceof Buffer) {
      Buffer buffer = (Buffer)result;
      result = buffer.subSequence(0, buffer.length());
    }
    return result;
  }

}
