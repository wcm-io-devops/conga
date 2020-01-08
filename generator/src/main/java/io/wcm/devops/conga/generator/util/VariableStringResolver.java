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

import io.wcm.devops.conga.generator.spi.context.ValueProviderGlobalContext;

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
  private static final String NAME_PATTERN_STRING = "[^\\}\\{\\$\\:()'\"/\\#,;\\+\\*@!\\^\\s]";
  private static final String NAME_PATTERN_STRING_NOT_EMPTY = NAME_PATTERN_STRING + "+";
  private static final String NAME_PATTERN_STRING_OR_EMPTY = NAME_PATTERN_STRING + "*";
  private static final String EXPRESSION_STRING = "[^\\}\\{]+";

  static final int EXPRESSION_POS_DOLLAR_SIGN = 1;
  static final int EXPRESSION_POS_EXPRESSION = 2;

  private static final int VARIABLE_POS_VARIABLE_1 = 2;
  private static final int VARIABLE_POS_VALUE_PROVIDER_NAME = 4;
  private static final int VARIABLE_POS_VARIABLE_2 = 5;
  private static final int VARIABLE_POS_DEFAULT_VALUE = 7;

  private static final String EXPRESSION_PATTERN = "(\\\\?\\$)"
      + "\\{(" + EXPRESSION_STRING + ")\\}";
  static final Pattern SINGLE_EXPRESSION_PATTERN = Pattern.compile("^" + EXPRESSION_PATTERN + "$");
  static final Pattern MULTI_EXPRESSION_PATTERN = Pattern.compile(EXPRESSION_PATTERN);
  private static final int REPLACEMENT_MAX_ITERATIONS = 20;

  /*
   * Either expect single strict variable name, or allow more complex expressions (e.g. jsonpath)
   * if a value provider plugin is referenced.
   */
  static final Pattern VARIABLE_PATTERN = Pattern.compile("((" + NAME_PATTERN_STRING_NOT_EMPTY + ")|"
      + "((" + NAME_PATTERN_STRING_NOT_EMPTY + ")\\:\\:)"
      + "([^\\}\\{]*?))"
      + "(\\:(" + NAME_PATTERN_STRING_OR_EMPTY + "))?");

  private final VariableResolver variableResolver;
  private final JexlResolver jexlResolver;

  /**
   * @param valueProviderGlobalContext Value provider global context
   * @param variableMapResolver Variable map resolver
   */
  public VariableStringResolver(ValueProviderGlobalContext valueProviderGlobalContext,
      VariableMapResolver variableMapResolver) {
    this.variableResolver = new VariableResolver(valueProviderGlobalContext);
    this.jexlResolver = new JexlResolver(variableMapResolver);
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
  public String resolveString(String value, Map<String, Object> variables) {
    Object result = resolve(value, variables);
    return ValueUtil.valueToString(result);
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
  public Object resolve(String value, Map<String, Object> variables) {
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
  public String resolveString(String value, Map<String, Object> variables, boolean deescapeVariables) {
    Object result = resolve(value, variables, deescapeVariables);
    return ValueUtil.valueToString(result);
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
  public Object resolve(String value, Map<String, Object> variables, boolean deescapeVariables) {
    if (value == null) {
      return null;
    }

    Object result = resolve(value, variables, 0);

    if (deescapeVariables && (result instanceof String)) {
      result = deescape((String)result);
    }

    return result;
  }

  /**
   * De-escapes all escaped variables in the given string.
   * @param value String that may contain escaped variable references (starting with \$)
   * @return String with de-escaped variable references.
   */
  public String deescape(String value) {
    return MULTI_EXPRESSION_PATTERN.matcher(value).replaceAll("\\$\\{"
        + "$" + EXPRESSION_POS_EXPRESSION + "\\}");
  }

  private Object resolve(String value, Map<String, Object> variables, int iterationCount) {
    if (iterationCount >= REPLACEMENT_MAX_ITERATIONS) {
      throw new IllegalArgumentException("Cyclic dependencies in variable string detected: " + value);
    }

    // check if variable string contains only single variable - in this case resolve and return value without necessarily converting it to a string
    Matcher matcherSingle = SINGLE_EXPRESSION_PATTERN.matcher(value);
    if (matcherSingle.matches()) {
      return resolveSingle(matcherSingle, variables, iterationCount);
    }
    else {
      Matcher matcherMulti = MULTI_EXPRESSION_PATTERN.matcher(value);
      return resolveMulti(matcherMulti, variables, iterationCount);
    }
  }

  private Object resolveSingle(Matcher matcher, Map<String, Object> variables, int iterationCount) {
    boolean escapedVariable = StringUtils.equals(matcher.group(EXPRESSION_POS_DOLLAR_SIGN), "\\$");
    String expression = matcher.group(EXPRESSION_POS_EXPRESSION);

    // keep escaped variables intact
    if (escapedVariable) {
      return matcher.group(0);
    }

    else {
      Matcher variableMatcher = VARIABLE_PATTERN.matcher(expression);

      // resolve variable
      if (variableMatcher.matches()) {
        String valueProviderName = variableMatcher.group(VARIABLE_POS_VALUE_PROVIDER_NAME);
        String variable = StringUtils.defaultString(variableMatcher.group(VARIABLE_POS_VARIABLE_1), variableMatcher.group(VARIABLE_POS_VARIABLE_2));
        String defaultValueString = variableMatcher.group(VARIABLE_POS_DEFAULT_VALUE);

        Object valueObject = variableResolver.resolve(valueProviderName, variable, defaultValueString, variables);
        if (valueObject != null) {
          if (valueObject instanceof String) {
            // try again until all nested references are resolved
            return resolve((String)valueObject, variables, iterationCount + 1);
          }
          else {
            return valueObject;
          }
        }
        else {
          throw new IllegalArgumentException("Unable to resolve variable: " + matcher.group(0));
        }
      }

      // resolve JEXL expression
      else {
        Object valueObject = jexlResolver.resolve(expression, variables);
        if (valueObject != null) {
          if (valueObject instanceof String) {
            // try again until all nested references are resolved
            return resolve((String)valueObject, variables, iterationCount + 1);
          }
          else {
            return valueObject;
          }
        }
        else {
          throw new IllegalArgumentException("Unable to resolve expression: " + matcher.group(0));
        }
      }
    }
  }

  private Object resolveMulti(Matcher matcher, Map<String, Object> variables, int iterationCount) {
    StringBuffer sb = new StringBuffer();
    boolean replacedAny = false;
    while (matcher.find()) {
      boolean escapedVariable = StringUtils.equals(matcher.group(EXPRESSION_POS_DOLLAR_SIGN), "\\$");
      String expression = matcher.group(EXPRESSION_POS_EXPRESSION);

      // keep escaped variables intact
      if (escapedVariable) {
        matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));
      }

      else {
        Matcher variableMatcher = VARIABLE_PATTERN.matcher(expression);

        // resolve variable
        if (variableMatcher.matches()) {
          String valueProviderName = variableMatcher.group(VARIABLE_POS_VALUE_PROVIDER_NAME);
          String variable = StringUtils.defaultString(variableMatcher.group(VARIABLE_POS_VARIABLE_1), variableMatcher.group(VARIABLE_POS_VARIABLE_2));
          String defaultValueString = variableMatcher.group(VARIABLE_POS_DEFAULT_VALUE);

          Object valueObject = variableResolver.resolve(valueProviderName, variable, defaultValueString, variables);
          if (valueObject != null) {
            String variableValue = ValueUtil.valueToString(valueObject);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(variableValue));
            replacedAny = true;
          }
          else {
            throw new IllegalArgumentException("Unable to resolve variable: " + matcher.group(0));
          }
        }

        // resolve JEXL expression
        else {
          Object valueObject = jexlResolver.resolve(expression, variables);
          if (valueObject != null) {
            String variableValue = ValueUtil.valueToString(valueObject);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(variableValue));
            replacedAny = true;
          }
          else {
            throw new IllegalArgumentException("Unable to resolve variable: " + matcher.group(0));
          }
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

  /**
   * Checks if the given string contains any reference to a variable from a value provider.
   * @param value Value string
   * @return true if a value provider reference was found.
   */
  public static boolean hasValueProviderReference(String value) {
    Matcher matcher = MULTI_EXPRESSION_PATTERN.matcher(value);
    while (matcher.find()) {
      String expression = matcher.group(EXPRESSION_POS_EXPRESSION);
      Matcher variableMatcher = VARIABLE_PATTERN.matcher(expression);
      if (variableMatcher.matches()) {
        String valueProviderName = variableMatcher.group(VARIABLE_POS_VALUE_PROVIDER_NAME);
        if (StringUtils.isNotEmpty(valueProviderName)) {
          return true;
        }
      }
    }
    return false;
  }

}
