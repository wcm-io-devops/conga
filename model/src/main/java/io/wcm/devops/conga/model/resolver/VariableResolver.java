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
package io.wcm.devops.conga.model.resolver;

import io.wcm.devops.conga.model.shared.Configurable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Resolve variables in all model levels including inheritance of variable contexts.
 * On any object implementing {@link Configurable} variables in the "config" section are resolved.
 */
public final class VariableResolver {

  private static final Pattern VARIABLE_PATTERN = Pattern.compile("(\\\\?\\$)\\{([^\\}\\{\\$]+)\\}");
  private static final int REPLACEMENT_MAX_ITERATIONS = 20;

  private VariableResolver() {
    // static methods only
  }

  /**
   * Resolve all variables.
   * @param model Model with variables at any nested level.
   */
  public static void resolve(Object model) {
    resolve(model, new HashMap<>());
  }

  private static void resolve(Object object, Map<String, Object> parentVariables) {
    if (object instanceof Map) {
      for (Object child : ((Map)object).values()) {
        resolve(child, parentVariables);
      }
      return;
    }
    if (object instanceof List) {
      for (Object child : (List)object) {
        resolve(child, parentVariables);
      }
      return;
    }
    Map<String, Object> variables;
    if (object instanceof Configurable) {
      variables = resolveConfigurable((Configurable)object, parentVariables);
    }
    else {
      variables = new HashMap<>();
    }
    resolveNestedObjects(object, variables);
  }

  private static void resolveNestedObjects(Object model, Map<String, Object> parentVariables) {
    try {
      Map<String, String> description = BeanUtils.describe(model);
      for (String propertyName : description.keySet()) {
        Object propertyValue = PropertyUtils.getProperty(model, propertyName);
        if (!StringUtils.equals(propertyName, "class")) {
          resolve(propertyValue, parentVariables);
        }
      }
    }
    catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
      throw new RuntimeException("Unable to get bean properties from '" + model.getClass().getName() + "'.", ex);
    }
  }

  private static Map<String, Object> resolveConfigurable(Configurable configurable, Map<String, Object> parentVariables) {
    Map<String, Object> variables = new HashMap<>();
    variables.putAll(parentVariables);
    if (configurable.getVariables() != null) {
      variables.putAll(configurable.getVariables());
    }
    Map<String, Object> resolvedConfig = resolveConfig(configurable.getConfig(), variables);

    configurable.resolved(resolvedConfig);

    return variables;
  }

  @SuppressWarnings("unchecked")
  private static Object resolveConfig(Object object, Map<String, Object> variables) {
    if (object instanceof Map) {
      return resolveConfig((Map<String, Object>)object, variables);
    }
    if (object instanceof List) {
      return resolveConfig((List<Object>)object, variables);
    }
    if (object instanceof String) {
      return resolveConfig((String)object, variables);
    }
    return object;
  }

  private static Map<String, Object> resolveConfig(Map<String, Object> map, Map<String, Object> variables) {
    Map<String, Object> resolvedMap = new HashMap<>();
    for (Entry<String, Object> entry : map.entrySet()) {
      resolvedMap.put(entry.getKey(), resolveConfig(entry.getValue(), variables));
    }
    return resolvedMap;
  }

  private static List<Object> resolveConfig(List<Object> list, Map<String, Object> variables) {
    List<Object> resolvedList = new ArrayList<>();
    for (Object object : list) {
      resolvedList.add(resolveConfig(object, variables));
    }
    return resolvedList;
  }

  private static String resolveConfig(String value, Map<String, Object> variables) {
    String resolvedString = resolveConfig_ReplaceVariables(value, variables, 0);

    // de-escaped escaped variables
    resolvedString = VARIABLE_PATTERN.matcher(resolvedString).replaceAll("\\$\\{$2\\}");

    return resolvedString;
  }

  private static String resolveConfig_ReplaceVariables(String value, Map<String, Object> variables, int iterationCount) {
    if (iterationCount >= REPLACEMENT_MAX_ITERATIONS) {
      throw new IllegalArgumentException("Cyclic dependencies in varaible string detected: " + value);
    }

    Matcher matcher = VARIABLE_PATTERN.matcher(value);
    StringBuffer sb = new StringBuffer();
    boolean replacedAny = false;
    while (matcher.find()) {
      boolean escapedVariable = StringUtils.equals(matcher.group(1), "\\$");
      String variable = matcher.group(2);
      if (escapedVariable) {
        matcher.appendReplacement(sb, Matcher.quoteReplacement("\\${" + variable + "}"));
      }
      else if (variables.containsKey(variable)) {
        Object variableValue = ObjectUtils.defaultIfNull(variables.get(variable), "");
        matcher.appendReplacement(sb, Matcher.quoteReplacement(variableValue.toString()));
        replacedAny = true;
      }
      else {
        throw new IllegalArgumentException("Unknown variable: " + variable);
      }
    }
    matcher.appendTail(sb);
    if (replacedAny) {
      return resolveConfig_ReplaceVariables(sb.toString(), variables, iterationCount + 1);
    }
    else {
      return sb.toString();
    }
  }

}
