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
package io.wcm.devops.conga.model.resolver.testmodel;


public class Root {

  private String prop1;
  private Integer prop2;
  private ConfScope1 scope1;

  public String getProp1() {
    return this.prop1;
  }

  public void setProp1(String prop1) {
    this.prop1 = prop1;
  }

  public Integer getProp2() {
    return this.prop2;
  }

  public void setProp2(Integer prop2) {
    this.prop2 = prop2;
  }

  public ConfScope1 getScope1() {
    return this.scope1;
  }

  public void setScope1(ConfScope1 scope1) {
    this.scope1 = scope1;
  }

}