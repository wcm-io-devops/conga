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

import org.apache.commons.lang3.StringUtils;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.ValueProviderPlugin;
import io.wcm.devops.conga.generator.spi.context.ValueProviderContext;
import io.wcm.devops.conga.generator.spi.context.ValueProviderGlobalContext;
import io.wcm.devops.conga.model.util.MapExpander;

/**
 * Resolve variables referencing an entry from a map.
 */
public final class VariableResolver {

  private final ValueProviderGlobalContext valueProviderGlobalContext;

  /**
   * @param context Value provider global context
   */
  public VariableResolver(ValueProviderGlobalContext context) {
    this.valueProviderGlobalContext = context;
  }

  /**
   * Resolves a variable.
   * @param valueProviderName Value provider name or null if local variable is referenced.
   * @param variable Variable name
   * @param defaultValueString Default value as string
   * @param variables Variable map
   * @return Variable value or null if none was found
   */
  public Object resolve(String valueProviderName, String variable, String defaultValueString, Map<String, Object> variables) {
    Object result;

    // resolve value from value provider
    if (StringUtils.isNotEmpty(valueProviderName)) {
      ValueProviderContext valueProviderContext = new ValueProviderContext()
          .valueProviderGlobalContext(valueProviderGlobalContext)
          .valueProviderName(valueProviderName);
      ValueProviderPlugin valueProvider = getValueProvider(valueProviderContext);
      result = valueProvider.resolve(variable, valueProviderContext);
    }

    // resolve value from variable map
    else {
      result = MapExpander.getDeep(variables, variable);
    }

    if (result == null && defaultValueString != null) {
      // fallback to default value
      result = ValueUtil.stringToValue(defaultValueString);
    }

    return result;
  }

  private ValueProviderPlugin getValueProvider(ValueProviderContext valueProviderContext) {
    String valueProviderPluginName = (String)valueProviderContext.getValueProviderConfig(ValueProviderGlobalContext.PARAM_PLUGIN_NAME);
    if (StringUtils.isBlank(valueProviderPluginName)) {
      valueProviderPluginName = valueProviderContext.getValueProviderName();
    }
    try {
      return valueProviderGlobalContext.getPluginManager().get(valueProviderPluginName, ValueProviderPlugin.class);
    }
    catch (GeneratorException ex) {
      throw new IllegalArgumentException("Unable to get value provider: " + valueProviderContext.getValueProviderName(), ex);
    }
  }

}
