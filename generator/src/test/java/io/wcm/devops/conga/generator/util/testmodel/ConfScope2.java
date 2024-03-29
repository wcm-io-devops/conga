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
package io.wcm.devops.conga.generator.util.testmodel;

import io.wcm.devops.conga.model.shared.AbstractConfigurable;

public class ConfScope2 extends AbstractConfigurable {
  private static final long serialVersionUID = -3341288196194211843L;

  private ConfScope3 scope31;
  private ConfScope3 scope32;

  public ConfScope3 getScope31() {
    return this.scope31;
  }

  public void setScope31(ConfScope3 scope31) {
    this.scope31 = scope31;
  }

  public ConfScope3 getScope32() {
    return this.scope32;
  }

  public void setScope32(ConfScope3 scope32) {
    this.scope32 = scope32;
  }

}
