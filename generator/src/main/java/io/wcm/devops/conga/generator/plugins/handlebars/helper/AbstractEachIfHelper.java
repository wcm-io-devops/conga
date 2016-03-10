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
package io.wcm.devops.conga.generator.plugins.handlebars.helper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.helper.EachHelper;
import com.google.common.collect.ImmutableList;

import io.wcm.devops.conga.generator.spi.handlebars.HelperPlugin;
import io.wcm.devops.conga.model.util.MapExpander;

/**
 * Handlebars helper that extends the each helper by iterating only on list items that match a certain condition.
 */
abstract class AbstractEachIfHelper implements HelperPlugin {

  private final Helper<Object> delegate = new EachHelper();
  private final BiFunction<Object, Options, Boolean> propertyEvaluator;

  AbstractEachIfHelper(BiFunction<Object, Options, Boolean> propertyEvaluator) {
    this.propertyEvaluator = propertyEvaluator;
  }

  @SuppressWarnings("unchecked")
  @Override
  public final CharSequence apply(Object context, Options options) throws IOException {
    if (context != null && !options.isFalsy(context)) {
      if (context instanceof Iterable) {
        String propertyName = options.param(0, null);
        if (StringUtils.isEmpty(propertyName)) {
          return options.inverse();
        }
        else {
          Iterable<Object> filteredItems = filterIterable((Iterable<Object>)context, propertyName, options);
          return delegate.apply(filteredItems, options);
        }
      }
      else {
        return apply(ImmutableList.of(context), options);
      }
    }
    return delegate.apply(context, options);
  }

  /**
   * Filter iterable to contain only items that have any value set for the given property name.
   * @param items Items
   * @param propertyName Property name
   * @param options Options
   * @return Filtered items
   */
  private Iterable<Object> filterIterable(Iterable<Object> items, String propertyName,Options options) {
    return StreamSupport.stream(items.spliterator(), false)
        .filter(item -> item != null)
        .filter(item -> checkProperty(item, propertyName, options))
        .collect(Collectors.toList());
  }

  private boolean checkProperty(Object item, String propertyName, Options options) {
    Map<String, Object> propertyMap = toMap(options.propertySet(item));
    Object value = MapExpander.getDeep(propertyMap, propertyName);
    return propertyEvaluator.apply(value, options);
  }

  private Map<String, Object> toMap(Set<Entry<String, Object>> entries) {
    Map<String, Object> map = new HashMap<>();
    for (Entry<String, Object> entry : entries) {
      map.put(entry.getKey(), entry.getValue());
    }
    return map;
  }

}
