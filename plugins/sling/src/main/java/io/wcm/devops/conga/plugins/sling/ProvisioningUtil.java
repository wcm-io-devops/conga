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
package io.wcm.devops.conga.plugins.sling;

import io.wcm.devops.conga.generator.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.provisioning.model.Model;
import org.apache.sling.provisioning.model.ModelUtility;
import org.apache.sling.provisioning.model.io.ModelReader;

final class ProvisioningUtil {

  private static final String FILE_EXTENSION = "txt";

  private ProvisioningUtil() {
    // static methods only
  }

  public static boolean isProvisioningFile(File file, String charset) {
    try {
      return FileUtil.matchesExtension(file, FILE_EXTENSION)
          && StringUtils.contains(FileUtils.readFileToString(file, charset), "[feature ");
    }
    catch (IOException ex) {
      return false;
    }
  }

  public static Model getModel(File file, String charset) throws IOException {
    try (InputStream is = new FileInputStream(file);
        Reader reader = new InputStreamReader(is, charset)) {
      Model model = ModelReader.read(reader, null);
      model = ModelUtility.getEffectiveModel(model, null);
      return model;
    }
  }

}
