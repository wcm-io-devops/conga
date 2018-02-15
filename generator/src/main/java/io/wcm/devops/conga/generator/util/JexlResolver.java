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
package io.wcm.devops.conga.generator.util;

import java.util.Map;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlException;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;

import io.wcm.devops.conga.generator.GeneratorException;

final class JexlResolver {

  private static final int CACHE_SIZE = 1024;

  private final JexlEngine jexl;

  JexlResolver() {
    this.jexl = new JexlBuilder()
        .cache(CACHE_SIZE)
        .create();
  }

  public Object resolve(String expressionString, Map<String, Object> variables) {
    try {
      JexlExpression expression = jexl.createExpression(expressionString);
      JexlContext context = new MapContext(variables);
      return expression.evaluate(context);
    }
    catch (JexlException ex) {
      throw new GeneratorException("Unable to parse expression: ${" + expressionString + "}: " + ex.getMessage(), ex);
    }
  }

}
