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
package io.wcm.devops.conga.model.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import io.wcm.devops.conga.model.shared.AbstractConfigurable;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;


public class VariableResolverTest {

  @Test
  public void testConfigListMap() {

    SimpleConf conf = new SimpleConf();
    conf.setVariables(ImmutableMap.of("var1", "v1", "var2", 55, "var3", true));
    conf.setConfig(ImmutableMap.of("key1", "${var1}", "list", ImmutableList.of(
        ImmutableMap.of("key2", "${var2}", "map", ImmutableMap.of("key3", "${var3}"))
        )));

    VariableResolver.resolve(conf);

    assertTrue(conf.isResolved());
    assertNull(conf.getVariables());
    assertEquals(ImmutableMap.of("key1", "v1", "list", ImmutableList.of(
        ImmutableMap.of("key2", "55", "map", ImmutableMap.of("key3", "true"))
        )), conf.getConfig());
  }

  @Test
  public void testNestedVariables() {

    SimpleConf conf = new SimpleConf();
    conf.setVariables(ImmutableMap.of("var1", "v1", "var2", "${var1}${var1}", "var3", "${var2}${var1}"));
    conf.setConfig(ImmutableMap.of("key1", "${var1},${var2},${var3}"));

    VariableResolver.resolve(conf);

    assertEquals(ImmutableMap.of("key1", "v1,v1v1,v1v1v1"), conf.getConfig());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNestedVariables_IllegalRecursion() {

    SimpleConf conf = new SimpleConf();
    conf.setVariables(ImmutableMap.of("var1", "${var2}", "var2", "${var1}"));
    conf.setConfig(ImmutableMap.of("key1", "${var1}"));

    VariableResolver.resolve(conf);
  }

  @Test
  public void testEscapedVariables() {

    SimpleConf conf = new SimpleConf();
    conf.setVariables(ImmutableMap.of("var1", "v1", "var2", "\\${var1}${var1}", "var3", "${var2}${var1}"));
    conf.setConfig(ImmutableMap.of("key1", "\\${var1},${var2},${var3}"));

    VariableResolver.resolve(conf);

    assertEquals(ImmutableMap.of("key1", "${var1},${var1}v1,${var1}v1v1"), conf.getConfig());
  }

  @Test
  public void testDeepNested() {
    Root root = new Root();
    ConfScope1 scope1 = new ConfScope1();
    ConfScope2 scope21 = new ConfScope2();
    ConfScope3 scope31 = new ConfScope3();
    ConfScope3 scope32 = new ConfScope3();
    scope21.setScope31(scope31);
    scope21.setScope32(scope32);
    ConfScope2 scope22 = new ConfScope2();
    scope1.setScope2(ImmutableList.of(scope21, scope22));
    root.setScope1(scope1);
    SimpleConf simple = new SimpleConf();
    scope1.setMap(ImmutableMap.of("simple", simple));

    root.setProp1("${var1} variable");
    root.setProp2(55);

    scope1.setVariables(ImmutableMap.of("var1", "v1"));
    scope1.setConfig(ImmutableMap.of("conf", "${var1} variable"));

    scope21.setVariables(ImmutableMap.of("var21", "v21"));
    scope21.setConfig(ImmutableMap.of("conf", "${var1} ${var21} variables"));

    scope22.setVariables(ImmutableMap.of("var22", "v22"));
    scope22.setConfig(ImmutableMap.of("conf", "${var1} ${var22} variables"));

    scope31.setProp3("${var1} variable");
    scope31.setVariables(ImmutableMap.of("var1", "v31"));
    scope31.setConfig(ImmutableMap.of("conf", "${var1} ${var21} variables"));

    scope32.setVariables(ImmutableMap.of("var21", "v32"));
    scope32.setConfig(ImmutableMap.of("conf", "${var1} ${var21} variables"));

    simple.setVariables(ImmutableMap.of("var21", "v21"));
    simple.setConfig(ImmutableMap.of("conf", "${var1} ${var21} variables"));


    VariableResolver.resolve(root);

    assertEquals("${var1} variable", root.getProp1());
    assertEquals((Integer)55, root.getProp2());

    assertNull(scope1.getVariables());
    assertEquals(ImmutableMap.of("conf", "v1 variable"), scope1.getConfig());

    assertNull(scope21.getVariables());
    assertEquals(ImmutableMap.of("conf", "v1 v21 variables"), scope21.getConfig());

    assertNull(scope22.getVariables());
    assertEquals(ImmutableMap.of("conf", "v1 v22 variables"), scope22.getConfig());

    assertEquals("${var1} variable", scope31.getProp3());
    assertNull(scope31.getVariables());
    assertEquals(ImmutableMap.of("conf", "v31 v21 variables"), scope31.getConfig());

    assertNull(scope32.getVariables());
    assertEquals(ImmutableMap.of("conf", "v1 v32 variables"), scope32.getConfig());

    assertNull(simple.getVariables());
    assertEquals(ImmutableMap.of("conf", "v1 v21 variables"), simple.getConfig());
  }


  public static class SimpleConf extends AbstractConfigurable {

    // no additional properties

  }

  public static class Root {

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

  public static class ConfScope1 extends AbstractConfigurable {

    private List<ConfScope2> scope2;
    private Map<String, Object> map;

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

  }

  public static class ConfScope2 extends AbstractConfigurable {

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

  public static class ConfScope3 extends AbstractConfigurable {

    private String prop3;

    public String getProp3() {
      return this.prop3;
    }

    public void setProp3(String prop3) {
      this.prop3 = prop3;
    }

  }

}
