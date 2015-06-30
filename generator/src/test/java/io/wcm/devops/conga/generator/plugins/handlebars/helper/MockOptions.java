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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.TagType;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.MapValueResolver;
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
        getContext(),
        getFnTemplate(),
        getInverseTemplate(),
        params,
        ImmutableMap.of());
  }

  private static Context getContext() {
    Context context = mock(Context.class);

    when(context.propertySet(anyObject())).thenAnswer(new Answer<Set<Entry<String, Object>>>() {
      @Override
      public Set<Entry<String, Object>> answer(InvocationOnMock invocation) throws Throwable {
        Object object = invocation.getArgumentAt(0, Object.class);
        return MapValueResolver.INSTANCE.propertySet(object);
      }
    });

    return context;
  }

  private static Template getFnTemplate() {
    Template template = mock(Template.class);
    try {
      when(template.apply(any(Context.class))).then(new Answer<String>() {
        @Override
        public String answer(InvocationOnMock invocation) throws Throwable {
          return getFnForContext(invocation.getArgumentAt(0, Context.class));
        }
      });
      when(template.apply(anyObject())).then(new Answer<String>() {
        @Override
        public String answer(InvocationOnMock invocation) throws Throwable {
          Object arg = invocation.getArgumentAt(0, Object.class);
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
    if (context == null || mockingDetails(context).isMock()) {
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
      when(template.apply(anyObject())).thenReturn(INVERSE_RETURN);
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return template;
  }

}
