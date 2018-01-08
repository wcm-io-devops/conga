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
package io.wcm.devops.conga.generator.handlebars;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.github.jknack.handlebars.EscapingStrategy;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.context.PluginContextOptions;
import io.wcm.devops.conga.generator.spi.handlebars.EscapingStrategyPlugin;
import io.wcm.devops.conga.generator.spi.handlebars.HelperPlugin;
import io.wcm.devops.conga.generator.spi.handlebars.context.EscapingStrategyContext;
import io.wcm.devops.conga.generator.spi.handlebars.context.HelperContext;
import io.wcm.devops.conga.generator.util.PluginManager;
import io.wcm.devops.conga.resource.ResourceCollection;

/**
 * Manages charset-aware handlebars instances.
 */
public class HandlebarsManager {

  private final List<ResourceCollection> templateDirs;
  private final PluginManager pluginManager;
  private final EscapingStrategyContext escapingStrategyContext;
  private final HelperContext helperContext;

  private final LoadingCache<HandlebarsKey, Handlebars> handlebarsCache =
      CacheBuilder.newBuilder().build(new CacheLoader<HandlebarsKey, Handlebars>() {
        @SuppressWarnings("unchecked")
        @Override
        public Handlebars load(HandlebarsKey options) throws Exception {

          // setup handlebars
          TemplateLoader templateLoader = new CharsetAwareTemplateLoader(templateDirs, options.getCharset());
          EscapingStrategyPlugin escapingStrategy = pluginManager.get(options.getEscapingStrategy(), EscapingStrategyPlugin.class);
          Handlebars handlebars = new Handlebars(templateLoader).with(new EscapingStrategy() {
            @Override
            public CharSequence escape(CharSequence value) {
              return escapingStrategy.escape(value, escapingStrategyContext);
            }
          });

          // register helper plugins
          pluginManager.getAll(HelperPlugin.class)
              .forEach(plugin -> handlebars.registerHelper(plugin.getName(), new Helper<Object>() {
                @Override
                public Object apply(Object context, Options helperOptions) throws IOException {
                  return plugin.apply(context, helperOptions, helperContext);
                }
              }));

          return handlebars;
        }
      });

  /**
   * @param templateDirs Template base directories
   * @param pluginContextOptions Plugin context options
   */
  public HandlebarsManager(List<ResourceCollection> templateDirs, PluginContextOptions pluginContextOptions) {
    this.templateDirs = templateDirs;
    this.pluginManager = pluginContextOptions.getPluginManager();
    this.escapingStrategyContext = new EscapingStrategyContext().pluginContextOptions(pluginContextOptions);
    this.helperContext = new HelperContext().pluginContextOptions(pluginContextOptions);
  }

  /**
   * Get handlebars instance with escaping for file extension and charset.
   * @param escapingStrategy Escaping strategy plugin name
   * @param charset Charset
   * @return Handlebars instance
   */
  public Handlebars get(String escapingStrategy, String charset) {
    HandlebarsKey key = new HandlebarsKey(escapingStrategy, charset);
    try {
      return handlebarsCache.get(key);
    }
    catch (ExecutionException ex) {
      throw new GeneratorException("Unable to get handlebars instance for " + key.toString(), ex);
    }
  }

}
