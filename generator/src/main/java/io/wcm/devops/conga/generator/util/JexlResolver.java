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

import static io.wcm.devops.conga.generator.util.VariableStringResolver.EXPRESSION_POS_DOLLAR_SIGN;
import static io.wcm.devops.conga.generator.util.VariableStringResolver.EXPRESSION_POS_EXPRESSION;
import static io.wcm.devops.conga.generator.util.VariableStringResolver.MULTI_EXPRESSION_PATTERN;
import static io.wcm.devops.conga.generator.util.VariableStringResolver.VARIABLE_PATTERN;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlException;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.lang3.StringUtils;

import io.wcm.devops.conga.generator.GeneratorException;

final class JexlResolver {

  private static final int CACHE_SIZE = 1024;

  private final JexlEngine jexl;
  private final VariableMapResolver variableMapResolver;

  JexlResolver(VariableMapResolver variableMapResolver) {
    this.jexl = new JexlBuilder()
        .cache(CACHE_SIZE)
        .create();
    this.variableMapResolver = variableMapResolver;
  }

  public Object resolve(String expressionString, Map<String, Object> variables) {
    Map<String, Object> resolvedVariables = resolveMapWithoutCycles(variables);
    try {
      JexlExpression expression = jexl.createExpression(expressionString);
      JexlContext context = new MapContext(resolvedVariables);
      return expression.evaluate(context);
    }
    catch (JexlException ex) {
      throw new GeneratorException("Unable to parse expression: ${" + expressionString + "}: " + ex.getMessage(), ex);
    }
  }

  private Map<String, Object> resolveMapWithoutCycles(Map<String, Object> variables) {
    Map<String, Object> variablesExcludedFromResolver = new HashMap<>();

    // remove variables with values containing expressions that are not simple variable references
    Map<String, Object> cleanedUpVariables = new HashMap<>();
    for (Map.Entry<String, Object> entry : variables.entrySet()) {
      if (entry.getValue() != null && !hasJexlExpresssions(entry.getValue().toString())) {
        cleanedUpVariables.put(entry.getKey(), entry.getValue());
      }
      else {
        variablesExcludedFromResolver.put(entry.getKey(), entry.getValue());
      }
    }

    // resolve simple variable references inside map
    Map<String, Object> resolvedVariables = variableMapResolver.resolve(cleanedUpVariables);

    // add back variables with complex expressions without resolving them
    resolvedVariables.putAll(variablesExcludedFromResolver);

    return resolvedVariables;
  }

  private boolean hasJexlExpresssions(String expressionString) {
    Matcher matcher = MULTI_EXPRESSION_PATTERN.matcher(expressionString);
    while (matcher.find()) {

      boolean escapedVariable = StringUtils.equals(matcher.group(EXPRESSION_POS_DOLLAR_SIGN), "\\$");
      String expression = matcher.group(EXPRESSION_POS_EXPRESSION);

      // keep escaped variables intact
      if (!escapedVariable) {
        Matcher variableMatcher = VARIABLE_PATTERN.matcher(expression);
        // if expression found that mathces not the variable pattern this string contains Jexl expressions
        if (!variableMatcher.matches()) {
          return true;
        }
      }

    }
    return false;
  }

}
