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
package io.wcm.devops.conga.generator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.wcm.devops.conga.generator.util.testmodel.ConfScope1;
import io.wcm.devops.conga.generator.util.testmodel.ConfScope2;
import io.wcm.devops.conga.generator.util.testmodel.ConfScope3;
import io.wcm.devops.conga.generator.util.testmodel.Root;
import io.wcm.devops.conga.generator.util.testmodel.SampleNode;
import io.wcm.devops.conga.generator.util.testmodel.SimpleConf;

class ConfigInheritanceResolverTest {

  @Test
  void testResolve() {
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
    scope1.setScope2(List.of(scope21, scope22));
    root.setScope1(scope1);
    SimpleConf simple1 = new SimpleConf();
    scope1.setMap(Map.of("simple", simple1));
    SampleNode sample = new SampleNode();
    SimpleConf simple2 = new SimpleConf();
    sample.setSimple(simple2);
    scope1.setSample(sample);

    scope1.setConfig(Map.of("conf", "s1"));

    scope21.setConfig(Map.of("conf2", "s21"));

    scope22.setConfig(Map.of("conf", "s22"));

    scope31.setConfig(Map.of("conf2", "s31"));

    scope32.setConfig(Map.of("conf3", "s32"));

    simple1.setConfig(Map.of("conf", "simple1"));

    simple2.setConfig(Map.of("conf2", "simple2"));


    ConfigInheritanceResolver.resolve(root);


    assertEquals(Map.of("conf", "s1"), scope1.getConfig());

    assertEquals(Map.of("conf", "s1", "conf2", "s21"), scope21.getConfig());

    assertEquals(Map.of("conf", "s22"), scope22.getConfig());

    assertEquals(Map.of("conf", "s1", "conf2", "s31"), scope31.getConfig());

    assertEquals(Map.of("conf", "s1", "conf2", "s21", "conf3", "s32"), scope32.getConfig());

    assertEquals(Map.of("conf", "simple1"), simple1.getConfig());

    assertEquals(Map.of("conf", "s1", "conf2", "simple2"), simple2.getConfig());
  }

}
