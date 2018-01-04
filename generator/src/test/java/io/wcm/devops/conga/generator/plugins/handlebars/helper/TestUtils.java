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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Options.Buffer;

final class TestUtils {

  private TestUtils() {
    // static methods only
  }

  public static void assertHelper(String expected, Helper<Object> helper, Object context, Options options) throws IOException {
    Object result = executeHelper(helper, context, options);
    assertEquals(expected, result);
  }

  public static Object executeHelper(Helper<Object> helper, Object context, Options options) throws IOException {
    Object result = helper.apply(context, options);
    if (result instanceof Buffer) {
      Buffer buffer = (Buffer)result;
      result = buffer.subSequence(0, buffer.length());
    }
    return result;
  }

}
