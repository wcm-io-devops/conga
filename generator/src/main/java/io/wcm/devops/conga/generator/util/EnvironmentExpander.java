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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;
import com.rits.cloning.Cloner;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.model.environment.Environment;
import io.wcm.devops.conga.model.environment.Node;

/**
 * Expands configuration nodes in environment.
 */
public final class EnvironmentExpander {

  private EnvironmentExpander() {
    // static methods only
  }

  /**
   * Environment definitions allows definition of nodes with multiple node names.
   * Expand them to nodes with single node names to simplify further processing.
   * @param environment Environment that may contain nodes with multiple node names.
   * @param environmentName Environment name
   * @return Environment that contains only nodes with single node names
   */
  public static Environment expandNodes(Environment environment, String environmentName) {
    Environment clonedEnvironemnt = new Cloner().deepClone(environment);

    clonedEnvironemnt.setNodes(environment.getNodes().stream()
        .flatMap(node -> getSingleNodes(node, environmentName))
        .collect(Collectors.toList()));

    return clonedEnvironemnt;
  }

  private static Stream<Node> getSingleNodes(Node node, String environmentName) {
    List<Node> nodes = new ArrayList<>();

    boolean hasNode = StringUtils.isNotEmpty(node.getNode());
    boolean hasNodes = !node.getNodes().isEmpty();

    if (!hasNode && !hasNodes) {
      throw new GeneratorException("Node without properties 'node' and 'nodes' found in '" + environmentName + "'.");
    }
    else if (hasNode && hasNodes) {
      throw new GeneratorException("Node with both properties 'node' and 'nodes' found in '" + environmentName + "'.");
    }
    else if (hasNode) {
      nodes.add(node);
    }
    else if (hasNodes) {
      for (String nodeName : node.getNodes()) {
        Node clonedNode = new Cloner().deepClone(node);
        clonedNode.setNode(nodeName);
        clonedNode.setNodes(ImmutableList.of());
        nodes.add(clonedNode);
      }
    }

    return nodes.stream();
  }

}
