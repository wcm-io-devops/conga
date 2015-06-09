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
import io.wcm.devops.conga.model.resolver.testmodel.ConfScope1;
import io.wcm.devops.conga.model.resolver.testmodel.ConfScope2;
import io.wcm.devops.conga.model.resolver.testmodel.ConfScope3;
import io.wcm.devops.conga.model.resolver.testmodel.Root;
import io.wcm.devops.conga.model.resolver.testmodel.SampleNode;
import io.wcm.devops.conga.model.resolver.testmodel.SimpleConf;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ConfigResolverTest {

  @Test
  public void testConfigListMap() {

    SimpleConf conf = new SimpleConf();
    conf.setVariables(ImmutableMap.of("var1", "v1", "var2", 55, "var3", true));
    conf.setConfig(ImmutableMap.of("key1", "${var1}", "list", ImmutableList.of(
        ImmutableMap.of("key2", "${var2}", "map", ImmutableMap.of("key3", "${var3}"))
        )));

    ConfigResolver.resolve(conf);

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

    ConfigResolver.resolve(conf);

    assertEquals(ImmutableMap.of("key1", "v1,v1v1,v1v1v1"), conf.getConfig());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNestedVariables_IllegalRecursion() {

    SimpleConf conf = new SimpleConf();
    conf.setVariables(ImmutableMap.of("var1", "${var2}", "var2", "${var1}"));
    conf.setConfig(ImmutableMap.of("key1", "${var1}"));

    ConfigResolver.resolve(conf);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnknownVariable() {

    SimpleConf conf = new SimpleConf();
    conf.setConfig(ImmutableMap.of("key1", "${var1}"));

    ConfigResolver.resolve(conf);
  }

  @Test(expected = IllegalStateException.class)
  public void testResolveResolved() {

    SimpleConf conf = new SimpleConf();

    ConfigResolver.resolve(conf);
    ConfigResolver.resolve(conf);
  }

  @Test
  public void testEscapedVariables() {

    SimpleConf conf = new SimpleConf();
    conf.setVariables(ImmutableMap.of("var1", "v1", "var2", "\\${var1}${var1}", "var3", "${var2}${var1}"));
    conf.setConfig(ImmutableMap.of("key1", "\\${var1},${var2},${var3}"));

    ConfigResolver.resolve(conf);

    assertEquals(ImmutableMap.of("key1", "${var1},${var1}v1,${var1}v1v1"), conf.getConfig());
  }

  @Test
  public void testDeepNested() {
    /*
     object tree:

     Root
       +- scope1
          +- [
            scope21
              +- scope31
              +- scope32
            scope22
          ]
          +- simple1
          +- sample
             +- simple2
     */

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
    SimpleConf simple1 = new SimpleConf();
    scope1.setMap(ImmutableMap.of("simple", simple1));
    SampleNode sample = new SampleNode();
    SimpleConf simple2 = new SimpleConf();
    sample.setSimple(simple2);
    scope1.setSample(sample);

    root.setProp1("${var1} variable");
    root.setProp2(55);

    scope1.setVariables(ImmutableMap.of("var1", "v1"));
    scope1.setConfig(ImmutableMap.of("conf", "${var1} variable"));

    scope21.setVariables(ImmutableMap.of("var21", "v21"));
    scope21.setConfig(ImmutableMap.of("conf2", "${var1} ${var21} variables"));

    scope22.setVariables(ImmutableMap.of("var22", "v22"));
    scope22.setConfig(ImmutableMap.of("conf", "${var1} ${var22} variables"));

    scope31.setProp3("${var1} variable");
    scope31.setVariables(ImmutableMap.of("var1", "v31"));
    scope31.setConfig(ImmutableMap.of("conf2", "${var1} ${var21} variables"));

    scope32.setVariables(ImmutableMap.of("var21", "v32"));
    scope32.setConfig(ImmutableMap.of("conf3", "${var1} ${var21} variables"));

    simple1.setVariables(ImmutableMap.of("var21", "v21"));
    simple1.setConfig(ImmutableMap.of("conf", "${var1} ${var21} variables"));

    simple2.setVariables(ImmutableMap.of("var1", "v99"));
    simple2.setConfig(ImmutableMap.of("conf2", "${var1} variable"));


    ConfigResolver.resolve(root);

    assertEquals("${var1} variable", root.getProp1());
    assertEquals((Integer)55, root.getProp2());

    assertNull(scope1.getVariables());
    assertEquals(ImmutableMap.of("conf", "v1 variable"), scope1.getConfig());

    assertNull(scope21.getVariables());
    assertEquals(ImmutableMap.of("conf", "v1 variable", "conf2", "v1 v21 variables"), scope21.getConfig());

    assertNull(scope22.getVariables());
    assertEquals(ImmutableMap.of("conf", "v1 v22 variables"), scope22.getConfig());

    assertEquals("${var1} variable", scope31.getProp3());
    assertNull(scope31.getVariables());
    assertEquals(ImmutableMap.of("conf", "v31 variable", "conf2", "v31 v21 variables"), scope31.getConfig());

    assertNull(scope32.getVariables());
    assertEquals(ImmutableMap.of("conf", "v1 variable", "conf2", "v1 v32 variables", "conf3", "v1 v32 variables"), scope32.getConfig());

    assertNull(simple1.getVariables());
    assertEquals(ImmutableMap.of("conf", "v1 v21 variables"), simple1.getConfig());

    assertNull(simple2.getVariables());
    assertEquals(ImmutableMap.of("conf", "v99 variable", "conf2", "v99 variable"), simple2.getConfig());
  }

}
