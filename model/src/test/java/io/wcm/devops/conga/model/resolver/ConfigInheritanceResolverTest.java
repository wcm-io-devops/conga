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
 * Tests {@link ConfigInheritanceResolver} and {@link VariableStringResolver}.
 */
public class ConfigInheritanceResolverTest {

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

    scope1.setConfig(ImmutableMap.of("conf", "s1"));

    scope21.setConfig(ImmutableMap.of("conf2", "s21"));

    scope22.setConfig(ImmutableMap.of("conf", "s22"));

    scope31.setConfig(ImmutableMap.of("conf2", "s31"));

    scope32.setConfig(ImmutableMap.of("conf3", "s32"));

    simple1.setConfig(ImmutableMap.of("conf", "simple1"));

    simple2.setConfig(ImmutableMap.of("conf2", "simple2"));


    ConfigInheritanceResolver.resolve(root);


    assertEquals(ImmutableMap.of("conf", "s1"), scope1.getConfig());

    assertEquals(ImmutableMap.of("conf", "s1", "conf2", "s21"), scope21.getConfig());

    assertEquals(ImmutableMap.of("conf", "s22"), scope22.getConfig());

    assertEquals(ImmutableMap.of("conf", "s1", "conf2", "s31"), scope31.getConfig());

    assertEquals(ImmutableMap.of("conf", "s1", "conf2", "s21", "conf3", "s32"), scope32.getConfig());

    assertEquals(ImmutableMap.of("conf", "simple1"), simple1.getConfig());

    assertEquals(ImmutableMap.of("conf", "s1", "conf2", "simple2"), simple2.getConfig());
  }

}
