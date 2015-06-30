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

import io.wcm.devops.conga.generator.spi.handlebars.HelperPlugin;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.helper.EachHelper;
import com.google.common.collect.ImmutableList;

/**
 * Handlebars helper that extends the each helper by iterating only on list items that have a specified property set to
 * a specific value.
 */
public class EachIfEqualsHelper implements HelperPlugin {

  private final Helper<Object> delegate = new EachHelper();

  /**
   * Plugin/Helper name
   */
  public static final String NAME = "eachIfEquals";

  @Override
  public String getName() {
    return NAME;
  }

  @SuppressWarnings("unchecked")
  @Override
  public CharSequence apply(Object context, Options options) throws IOException {
    if (context != null && context instanceof Iterable && !options.isFalsy(context)) {
      if (context instanceof Iterable) {
        String propertyName = options.param(0, null);
        if (StringUtils.isEmpty(propertyName)) {
          return options.inverse();
        }
        else {
          Object value = options.param(1, null);
          if (StringUtils.isNoneEmpty(propertyName)) {
            Iterable<Object> filteredItems = filterIterable((Iterable<Object>)context, propertyName, value, options);
            return delegate.apply(filteredItems, options);
          }
        }
      }
      else {
        return apply(ImmutableList.of(context), options);
      }
    }
    return delegate.apply(context, options);
  }

  /**
   * Filter iterable to contain only items that have the given value set for the given property name.
   * @param items Items
   * @param propertyName Property name
   * @param value Value
   * @param options Options
   * @return Filtered items
   */
  private Iterable<Object> filterIterable(Iterable<Object> items, String propertyName, Object value, Options options) {
    return StreamSupport.stream(items.spliterator(), false)
        .filter(item -> item != null)
        .filter(item -> hasPropertySet(item, propertyName, value, options))
        .collect(Collectors.toList());
  }

  private boolean hasPropertySet(Object item, String propertyName, Object value, Options options) {
    Set<Entry<String, Object>> propertySet = options.propertySet(item);
    for (Entry<String, Object> entry : propertySet) {
      if (StringUtils.equals(entry.getKey(), propertyName) && Objects.equals(entry.getValue(), value)) {
        return true;
      }
    }
    return false;
  }

}
