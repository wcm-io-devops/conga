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
package io.wcm.devops.conga.generator.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import io.wcm.devops.conga.generator.spi.context.ValueProviderContext;

/**
 * Resolve variables in a string referencing entries from a map.
 */
public final class VariableStringResolver {

  /*
   * variable pattern examples:
   * ${var1}
   * \${var1}
   * ${var1:defaultValue}
   * ${provider::var1}
   * ${provider::Var1:defaultValue}
   */
  private static final String NAME_PATTERN_STRING = "[^\\}\\{\\$\\:]+";
  private static final String VARIABLE_PATTERN_STRING = "(\\\\?\\$)"
      + "\\{((" + NAME_PATTERN_STRING + ")\\:\\:)?"
      + "(" + NAME_PATTERN_STRING + ")"
      + "(\\:(" + NAME_PATTERN_STRING + "))?\\}";

  static final int PATTERN_POS_DOLLAR_SIGN = 1;
  static final int PATTERN_POS_VALUE_PROVIDER_NAME_WITH_COLON = 2;
  static final int PATTERN_POS_VALUE_PROVIDER_NAME = 3;
  static final int PATTERN_POS_VARIABLE = 4;
  static final int PATTERN_POS_DEFAULT_VALUE_WITH_COLON = 5;
  static final int PATTERN_POS_DEFAULT_VALUE = 6;

  static final Pattern SINGLE_VARIABLE_PATTERN = Pattern.compile("^" + VARIABLE_PATTERN_STRING + "$");
  static final Pattern MULTI_VARIABLE_PATTERN = Pattern.compile(VARIABLE_PATTERN_STRING);
  private static final int REPLACEMENT_MAX_ITERATIONS = 20;

  private final VariableResolver variableResolver;

  /**
   * @param context Value provider context
   */
  public VariableStringResolver(ValueProviderContext context) {
    this.variableResolver = new VariableResolver(context);
  }

  /**
   * Replace variable placeholders in a string with syntax ${key} with values from a map.
   * The variables can recursively reference each other.
   * All escaped variables are deescaped.
   * @param value Value with variable placeholders
   * @param variables Variable map
   * @return Value with variable placeholders resolved.
   * @throws IllegalArgumentException when a variable name could not be resolve.d
   */
  public String resolve(String value, Map<String, Object> variables) {
    return resolve(value, variables, true);
  }

  /**
   * Replace variable placeholders in a string with syntax ${key} with values from a map.
   * The variables can recursively reference each other.
   * @param value Value with variable placeholders
   * @param variables Variable map
   * @param deescapeVariables If true, {@link #deescape(String)} is applied to the result string
   * @return Value with variable placeholders resolved.
   * @throws IllegalArgumentException when a variable name could not be resolve.d
   */
  public String resolve(String value, Map<String, Object> variables, boolean deescapeVariables) {
    if (value == null) {
      return null;
    }

    String resolvedString = resolve(value, variables, 0);

    if (deescapeVariables) {
      resolvedString = deescape(resolvedString);
    }

    return resolvedString;
  }

  /**
   * De-escapes all escaped variables in the given string.
   * @param value String that may contain escaped variable references (starting with \$)
   * @return String with de-escaped variable references.
   */
  public String deescape(String value) {
    return MULTI_VARIABLE_PATTERN.matcher(value).replaceAll("\\$\\{"
        + "$" + PATTERN_POS_VALUE_PROVIDER_NAME_WITH_COLON
        + "$" + PATTERN_POS_VARIABLE
        + "$" + PATTERN_POS_DEFAULT_VALUE_WITH_COLON + "\\}");
  }

  private String resolve(String value, Map<String, Object> variables, int iterationCount) {
    if (iterationCount >= REPLACEMENT_MAX_ITERATIONS) {
      throw new IllegalArgumentException("Cyclic dependencies in variable string detected: " + value);
    }

    Matcher matcher = MULTI_VARIABLE_PATTERN.matcher(value);
    StringBuffer sb = new StringBuffer();
    boolean replacedAny = false;
    while (matcher.find()) {
      boolean escapedVariable = StringUtils.equals(matcher.group(PATTERN_POS_DOLLAR_SIGN), "\\$");
      String valueProviderName = matcher.group(PATTERN_POS_VALUE_PROVIDER_NAME);
      String variable = matcher.group(PATTERN_POS_VARIABLE);
      String defaultValueString = matcher.group(PATTERN_POS_DEFAULT_VALUE);

      // keep escaped variables intact
      if (escapedVariable) {
        matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));
      }

      // resolve variable
      else {
        Object valueObject = variableResolver.resolve(valueProviderName, variable, defaultValueString, variables);
        if (valueObject != null) {
          String variableValue = ValueUtil.valueToString(valueObject);
          matcher.appendReplacement(sb, Matcher.quoteReplacement(variableValue.toString()));
          replacedAny = true;
        }
        else {
          throw new IllegalArgumentException("Unable to resolve variable: " + matcher.group(0));
        }
      }

    }
    matcher.appendTail(sb);
    if (replacedAny) {
      // try again until all nested references are resolved
      return resolve(sb.toString(), variables, iterationCount + 1);
    }
    else {
      return sb.toString();
    }
  }

}
