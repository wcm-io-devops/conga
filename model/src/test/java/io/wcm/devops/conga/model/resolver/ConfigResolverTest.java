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

/**
 * Tests {@link ConfigResolver} and {@link VariableResolver}.
 */
public class ConfigResolverTest {

  @Test
  public void testConfigListMap() {

    SimpleConf conf = new SimpleConf();
    conf.setConfig(ImmutableMap.of("var1", "v1", "var2", 55, "var3", true,
        "key1", "${var1}", "list", ImmutableList.of(
            ImmutableMap.of("key2", "${var2}", "map", ImmutableMap.of("key3", "${var3}"))
            )));

    ConfigResolver.resolve(conf);

    assertTrue(conf.isResolved());
    assertEquals(ImmutableMap.of("var1", "v1", "var2", 55, "var3", true,
        "key1", "v1", "list", ImmutableList.of(
            ImmutableMap.of("key2", "55", "map", ImmutableMap.of("key3", "true"))
            )), conf.getConfig());
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

    scope1.setConfig(ImmutableMap.of("var1", "v1",
        "conf", "${var1} variable"));

    scope21.setConfig(ImmutableMap.of("var21", "v21",
        "conf2", "${var1} ${var21} variables"));

    scope22.setConfig(ImmutableMap.of("var22", "v22",
        "conf", "${var1} ${var22} variables"));

    scope31.setProp3("${var1} variable");
    scope31.setConfig(ImmutableMap.of("var1", "v31",
        "conf2", "${var1} ${var21} variables"));

    scope32.setConfig(ImmutableMap.of("var21", "v32",
        "conf3", "${var1} ${var21} variables"));

    simple1.setConfig(ImmutableMap.of("var21", "v21",
        "conf", "${var1} ${var21} variables"));

    simple2.setConfig(ImmutableMap.of("var1", "v99",
        "conf2", "${var1} variable"));


    ConfigResolver.resolve(root);

    assertEquals("${var1} variable", root.getProp1());
    assertEquals((Integer)55, root.getProp2());

    assertEquals(ImmutableMap.of("var1", "v1",
        "conf", "v1 variable"), scope1.getConfig());

    assertEquals(ImmutableMap.of("var1", "v1", "var21", "v21",
        "conf", "v1 variable", "conf2", "v1 v21 variables"), scope21.getConfig());

    assertEquals(ImmutableMap.of("var1", "v1", "var22", "v22",
        "conf", "v1 v22 variables"), scope22.getConfig());

    assertEquals("${var1} variable", scope31.getProp3());
    assertEquals(ImmutableMap.of("var1", "v31", "var21", "v21",
        "conf", "v31 variable", "conf2", "v31 v21 variables"), scope31.getConfig());

    assertEquals(ImmutableMap.of("var1", "v1", "var21", "v32",
        "conf", "v1 variable", "conf2", "v1 v32 variables", "conf3", "v1 v32 variables"), scope32.getConfig());

    assertEquals(ImmutableMap.of("var1", "v1", "var21", "v21",
        "conf", "v1 v21 variables"), simple1.getConfig());

    assertEquals(ImmutableMap.of("var1", "v99",
        "conf", "v99 variable", "conf2", "v99 variable"), simple2.getConfig());
  }

}
