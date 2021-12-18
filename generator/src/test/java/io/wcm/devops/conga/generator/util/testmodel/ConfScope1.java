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

import java.util.List;
import java.util.Map;

import io.wcm.devops.conga.model.shared.AbstractConfigurable;

public class ConfScope1 extends AbstractConfigurable {
  private static final long serialVersionUID = 2765118713968544969L;

  private List<ConfScope2> scope2;
  private Map<String, Object> map;
  private SampleNode sample;

  public List<ConfScope2> getScope2() {
    return this.scope2;
  }

  public void setScope2(List<ConfScope2> scope2) {
    this.scope2 = scope2;
  }

  public Map<String, Object> getMap() {
    return this.map;
  }

  public void setMap(Map<String, Object> map) {
    this.map = map;
  }

  public SampleNode getSample() {
    return this.sample;
  }

  public void setSample(SampleNode sample) {
    this.sample = sample;
  }

}
