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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.TagType;
import com.github.jknack.handlebars.Template;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Subclass of {@link Options} that mocks all dependencies, but allows to set params.
 */
public final class MockOptions extends Options {

  /**
   * Return value for "fn()" template.
   */
  public static final String FN_RETURN = "fn";

  /**
   * Return value for "inverse()" template.
   */
  public static final String INVERSE_RETURN = "";

  private Map<String, Object> properties = new HashMap<String, Object>();

  /**
   * Options without any param
   */
  public MockOptions() {
    this(new Object[0]);
  }

  /**
   * Options with one or multiple params
   * @param params Params
   */
  public MockOptions(Object... params) {
    super(mock(Handlebars.class),
        "dummyHelperName",
        TagType.VAR,
        Context.newBuilder(null).build(),
        getFnTemplate(),
        getInverseTemplate(),
        params,
        ImmutableMap.of(),
        ImmutableList.of());
  }

  private static Template getFnTemplate() {
    Template template = mock(Template.class);
    try {
      when(template.apply(any(Context.class))).then(new Answer<String>() {
        @Override
        public String answer(InvocationOnMock invocation) throws Throwable {
          return getFnForContext(invocation.getArgument(0));
        }
      });
      when(template.apply(any())).then(new Answer<String>() {
        @Override
        public String answer(InvocationOnMock invocation) throws Throwable {
          Object arg = invocation.getArgument(0);
          if (arg instanceof Context) {
            return getFnForContext((Context)arg);
          }
          return FN_RETURN;
        }
      });
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return template;
  }

  private static String getFnForContext(Context context) {
    if (context == null || context.model() == null || mockingDetails(context).isMock()) {
      return FN_RETURN;
    }
    else {
      return FN_RETURN + "(" + context.toString() + ")";
    }
  }

  private static Template getInverseTemplate() {
    Template template = mock(Template.class);
    try {
      when(template.apply(any(Context.class))).thenReturn(INVERSE_RETURN);
      when(template.apply(any())).thenReturn(INVERSE_RETURN);
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return template;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(String name, T defaultValue) {
    Object value = properties.get(name);
    if (value == null) {
      value = defaultValue;
    }
    return (T)value;
  }

  /**
   * Set property.
   * @param name Name
   * @param value Value
   * @return this
   */
  public MockOptions property(String name, Object value) {
    properties.put(name, value);
    return this;
  }

}
