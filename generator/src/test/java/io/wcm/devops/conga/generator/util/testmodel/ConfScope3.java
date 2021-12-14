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

public class ConfScope3 extends AbstractConfigurable {
  private static final long serialVersionUID = -8001840561161642209L;

  private String prop3;

  public String getProp3() {
    return this.prop3;
  }

  public void setProp3(String prop3) {
    this.prop3 = prop3;
  }

}
