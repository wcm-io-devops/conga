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
package io.wcm.devops.conga.tooling.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Packages the definitions in a JAR file.
 */
@Mojo(name = "definition-package", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true)
public class DefinitionPackageMojo extends AbstractCongaMojo {

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    // build a JAR file with all CONGA definitions and resources
    buildDefinitionsJarFile();
  }

}
