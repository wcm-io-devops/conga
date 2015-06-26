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

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.TagType;
import com.github.jknack.handlebars.Template;
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
   * Options without any param
   */
  public MockOptions() {
    this(new Object[0]);
  }

  /**
   * Options with one param
   * @param param Param
   */
  public MockOptions(Object param) {
    this(new Object[] {
        param
    });
  }

  /**
   * Options with multiple params
   * @param params Params
   */
  public MockOptions(Object[] params) {
    super(mock(Handlebars.class),
        "dummyHelperName",
        TagType.VAR,
        mock(Context.class),
        getFnTemplate(),
        getInverseTemplate(),
        params,
        ImmutableMap.of());
  }

  private static Template getFnTemplate() {
    Template template = mock(Template.class);
    try {
      when(template.apply(anyObject())).thenReturn(FN_RETURN);
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return template;
  }

  private static Template getInverseTemplate() {
    return mock(Template.class);
  }

}
