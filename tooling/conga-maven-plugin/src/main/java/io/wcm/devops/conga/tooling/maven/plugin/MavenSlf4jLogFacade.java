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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.logging.Log;
import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Facade to rout SLF4J logging calls to maven plugin logger.
 */
class MavenSlf4jLogFacade implements Logger {

  private static final Pattern ARGUMENT_PATTERN = Pattern.compile("\\{\\}");

  private final Log log;

  public MavenSlf4jLogFacade(Log log) {
    this.log = log;
  }

  @Override
  public boolean isDebugEnabled() {
    return log.isDebugEnabled();
  }

  @Override
  public void debug(String content) {
    log.debug(content);
  }

  @Override
  public void debug(String content, Throwable error) {
    log.debug(content, error);
  }

  @Override
  public boolean isInfoEnabled() {
    return log.isInfoEnabled();
  }

  @Override
  public void info(String content) {
    log.info(content);
  }

  @Override
  public void info(String content, Throwable error) {
    log.info(content, error);
  }

  @Override
  public boolean isWarnEnabled() {
    return log.isWarnEnabled();
  }

  @Override
  public void warn(String content) {
    log.warn(content);
  }

  @Override
  public void warn(String content, Throwable error) {
    log.warn(content, error);
  }

  @Override
  public boolean isErrorEnabled() {
    return log.isErrorEnabled();
  }

  @Override
  public void error(String content) {
    log.error(content);
  }

  @Override
  public void error(String content, Throwable error) {
    log.error(content, error);
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public boolean isTraceEnabled() {
    return false;
  }

  @Override
  public void trace(String msg) {
    // ignore

  }

  @Override
  public void trace(String format, Object arg) {
    // ignore
  }

  @Override
  public void trace(String format, Object arg1, Object arg2) {
    // ignore
  }

  @Override
  public void trace(String format, Object... arguments) {
    // ignore
  }

  @Override
  public void trace(String msg, Throwable t) {
    // ignore
  }

  @Override
  public boolean isTraceEnabled(Marker marker) {
    return false;
  }

  @Override
  public void trace(Marker marker, String msg) {
    // ignore
  }

  @Override
  public void trace(Marker marker, String format, Object arg) {
    // ignore
  }

  @Override
  public void trace(Marker marker, String format, Object arg1, Object arg2) {
    // ignore
  }

  @Override
  public void trace(Marker marker, String format, Object... argArray) {
    // ignore
  }

  @Override
  public void trace(Marker marker, String msg, Throwable t) {
    // ignore
  }

  @Override
  public void debug(String format, Object arg) {
    debug(formatMessage(format, arg));
  }

  @Override
  public void debug(String format, Object arg1, Object arg2) {
    debug(formatMessage(format, arg1, arg2));
  }

  @Override
  public void debug(String format, Object... arguments) {
    debug(formatMessage(format, arguments));
  }

  @Override
  public boolean isDebugEnabled(Marker marker) {
    return isDebugEnabled();
  }

  @Override
  public void debug(Marker marker, String msg) {
    debug(msg);
  }

  @Override
  public void debug(Marker marker, String format, Object arg) {
    debug(format, arg);
  }

  @Override
  public void debug(Marker marker, String format, Object arg1, Object arg2) {
    debug(format, arg1, arg2);
  }

  @Override
  public void debug(Marker marker, String format, Object... arguments) {
    debug(format, arguments);
  }

  @Override
  public void debug(Marker marker, String msg, Throwable t) {
    debug(msg, t);
  }

  @Override
  public void info(String format, Object arg) {
    info(formatMessage(format, arg));
  }

  @Override
  public void info(String format, Object arg1, Object arg2) {
    info(formatMessage(format, arg1, arg2));
  }

  @Override
  public void info(String format, Object... arguments) {
    info(formatMessage(format, arguments));
  }

  @Override
  public boolean isInfoEnabled(Marker marker) {
    return isInfoEnabled();
  }

  @Override
  public void info(Marker marker, String msg) {
    info(msg);
  }

  @Override
  public void info(Marker marker, String format, Object arg) {
    info(format, arg);
  }

  @Override
  public void info(Marker marker, String format, Object arg1, Object arg2) {
    info(format, arg1, arg2);
  }

  @Override
  public void info(Marker marker, String format, Object... arguments) {
    info(format, arguments);
  }

  @Override
  public void info(Marker marker, String msg, Throwable t) {
    info(msg, t);
  }

  @Override
  public void warn(String format, Object arg) {
    warn(formatMessage(format, arg));
  }

  @Override
  public void warn(String format, Object... arguments) {
    warn(formatMessage(format, arguments));
  }

  @Override
  public void warn(String format, Object arg1, Object arg2) {
    warn(formatMessage(format, arg1, arg2));
  }

  @Override
  public boolean isWarnEnabled(Marker marker) {
    return isWarnEnabled();
  }

  @Override
  public void warn(Marker marker, String msg) {
    warn(msg);
  }

  @Override
  public void warn(Marker marker, String format, Object arg) {
    warn(format, format, arg);
  }

  @Override
  public void warn(Marker marker, String format, Object arg1, Object arg2) {
    warn(format, format, arg1, arg2);
  }

  @Override
  public void warn(Marker marker, String format, Object... arguments) {
    warn(format, format, arguments);
  }

  @Override
  public void warn(Marker marker, String msg, Throwable t) {
    warn(msg, t);
  }

  @Override
  public void error(String format, Object arg) {
    error(formatMessage(format, arg));
  }

  @Override
  public void error(String format, Object arg1, Object arg2) {
    error(formatMessage(format, arg1, arg2));
  }

  @Override
  public void error(String format, Object... arguments) {
    error(formatMessage(format, arguments));
  }

  @Override
  public boolean isErrorEnabled(Marker marker) {
    return isErrorEnabled();
  }

  @Override
  public void error(Marker marker, String msg) {
    error(msg);
  }

  @Override
  public void error(Marker marker, String format, Object arg) {
    error(format, arg);
  }

  @Override
  public void error(Marker marker, String format, Object arg1, Object arg2) {
    error(format, arg1, arg2);
  }

  @Override
  public void error(Marker marker, String format, Object... arguments) {
    error(format, arguments);
  }

  @Override
  public void error(Marker marker, String msg, Throwable t) {
    error(msg, t);
  }

  static String formatMessage(String format, Object... arguments) {
    StringBuffer sb = new StringBuffer();
    int index = 0;
    Matcher matcher = ARGUMENT_PATTERN.matcher(format);
    while (matcher.find()) {
      String value;
      if (arguments.length > index && arguments[index] != null) {
        value = arguments[index].toString();
      }
      else {
        value = "{}";
      }
      matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
      index++;
    }
    matcher.appendTail(sb);
    return sb.toString();
  }

}
