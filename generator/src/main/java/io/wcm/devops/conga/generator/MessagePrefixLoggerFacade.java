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
package io.wcm.devops.conga.generator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Logger wrapper that adds the given prefix (e.g. extra whitespaces) in front of every log message.
 */
class MessagePrefixLoggerFacade implements Logger {

  private final Logger delegate;
  private final String messagePrefix;

  public MessagePrefixLoggerFacade(Logger delegate, String messagePrefix) {
    this.delegate = delegate;
    this.messagePrefix = messagePrefix;
  }

  private String prefix(String txt) {
    return messagePrefix + StringUtils.defaultString(txt);
  }

  @Override
  public String getName() {
    return this.delegate.getName();
  }

  @Override
  public boolean isTraceEnabled() {
    return this.delegate.isTraceEnabled();
  }

  @Override
  public void trace(String msg) {
    this.delegate.trace(prefix(msg));
  }

  @Override
  public void trace(String format, Object arg) {
    this.delegate.trace(prefix(format), arg);
  }

  @Override
  public void trace(String format, Object arg1, Object arg2) {
    this.delegate.trace(prefix(format), arg1, arg2);
  }

  @Override
  public void trace(String format, Object... arguments) {
    this.delegate.trace(prefix(format), arguments);
  }

  @Override
  public void trace(String msg, Throwable t) {
    this.delegate.trace(prefix(msg), t);
  }

  @Override
  public boolean isTraceEnabled(Marker marker) {
    return this.delegate.isTraceEnabled(marker);
  }

  @Override
  public void trace(Marker marker, String msg) {
    this.delegate.trace(marker, prefix(msg));
  }

  @Override
  public void trace(Marker marker, String format, Object arg) {
    this.delegate.trace(marker, prefix(format), arg);
  }

  @Override
  public void trace(Marker marker, String format, Object arg1, Object arg2) {
    this.delegate.trace(marker, prefix(format), arg1, arg2);
  }

  @Override
  public void trace(Marker marker, String format, Object... argArray) {
    this.delegate.trace(marker, prefix(format), argArray);
  }

  @Override
  public void trace(Marker marker, String msg, Throwable t) {
    this.delegate.trace(marker, prefix(msg), t);
  }

  @Override
  public boolean isDebugEnabled() {
    return this.delegate.isDebugEnabled();
  }

  @Override
  public void debug(String msg) {
    this.delegate.debug(prefix(msg));
  }

  @Override
  public void debug(String format, Object arg) {
    this.delegate.debug(prefix(format), arg);
  }

  @Override
  public void debug(String format, Object arg1, Object arg2) {
    this.delegate.debug(prefix(format), arg1, arg2);
  }

  @Override
  public void debug(String format, Object... arguments) {
    this.delegate.debug(prefix(format), arguments);
  }

  @Override
  public void debug(String msg, Throwable t) {
    this.delegate.debug(prefix(msg), t);
  }

  @Override
  public boolean isDebugEnabled(Marker marker) {
    return this.delegate.isDebugEnabled(marker);
  }

  @Override
  public void debug(Marker marker, String msg) {
    this.delegate.debug(marker, prefix(msg));
  }

  @Override
  public void debug(Marker marker, String format, Object arg) {
    this.delegate.debug(marker, prefix(format), arg);
  }

  @Override
  public void debug(Marker marker, String format, Object arg1, Object arg2) {
    this.delegate.debug(marker, prefix(format), arg1, arg2);
  }

  @Override
  public void debug(Marker marker, String format, Object... arguments) {
    this.delegate.debug(marker, prefix(format), arguments);
  }

  @Override
  public void debug(Marker marker, String msg, Throwable t) {
    this.delegate.debug(marker, prefix(msg), t);
  }

  @Override
  public boolean isInfoEnabled() {
    return this.delegate.isInfoEnabled();
  }

  @Override
  public void info(String msg) {
    this.delegate.info(prefix(msg));
  }

  @Override
  public void info(String format, Object arg) {
    this.delegate.info(prefix(format), arg);
  }

  @Override
  public void info(String format, Object arg1, Object arg2) {
    this.delegate.info(prefix(format), arg1, arg2);
  }

  @Override
  public void info(String format, Object... arguments) {
    this.delegate.info(prefix(format), arguments);
  }

  @Override
  public void info(String msg, Throwable t) {
    this.delegate.info(prefix(msg), t);
  }

  @Override
  public boolean isInfoEnabled(Marker marker) {
    return this.delegate.isInfoEnabled(marker);
  }

  @Override
  public void info(Marker marker, String msg) {
    this.delegate.info(marker, prefix(msg));
  }

  @Override
  public void info(Marker marker, String format, Object arg) {
    this.delegate.info(marker, prefix(format), arg);
  }

  @Override
  public void info(Marker marker, String format, Object arg1, Object arg2) {
    this.delegate.info(marker, prefix(format), arg1, arg2);
  }

  @Override
  public void info(Marker marker, String format, Object... arguments) {
    this.delegate.info(marker, prefix(format), arguments);
  }

  @Override
  public void info(Marker marker, String msg, Throwable t) {
    this.delegate.info(marker, prefix(msg), t);
  }

  @Override
  public boolean isWarnEnabled() {
    return this.delegate.isWarnEnabled();
  }

  @Override
  public void warn(String msg) {
    this.delegate.warn(prefix(msg));
  }

  @Override
  public void warn(String format, Object arg) {
    this.delegate.warn(prefix(format), arg);
  }

  @Override
  public void warn(String format, Object... arguments) {
    this.delegate.warn(prefix(format), arguments);
  }

  @Override
  public void warn(String format, Object arg1, Object arg2) {
    this.delegate.warn(prefix(format), arg1, arg2);
  }

  @Override
  public void warn(String msg, Throwable t) {
    this.delegate.warn(prefix(msg), t);
  }

  @Override
  public boolean isWarnEnabled(Marker marker) {
    return this.delegate.isWarnEnabled(marker);
  }

  @Override
  public void warn(Marker marker, String msg) {
    this.delegate.warn(marker, prefix(msg));
  }

  @Override
  public void warn(Marker marker, String format, Object arg) {
    this.delegate.warn(marker, prefix(format), arg);
  }

  @Override
  public void warn(Marker marker, String format, Object arg1, Object arg2) {
    this.delegate.warn(marker, prefix(format), arg1, arg2);
  }

  @Override
  public void warn(Marker marker, String format, Object... arguments) {
    this.delegate.warn(marker, prefix(format), arguments);
  }

  @Override
  public void warn(Marker marker, String msg, Throwable t) {
    this.delegate.warn(marker, prefix(msg), t);
  }

  @Override
  public boolean isErrorEnabled() {
    return this.delegate.isErrorEnabled();
  }

  @Override
  public void error(String msg) {
    this.delegate.error(prefix(msg));
  }

  @Override
  public void error(String format, Object arg) {
    this.delegate.error(prefix(format), arg);
  }

  @Override
  public void error(String format, Object arg1, Object arg2) {
    this.delegate.error(prefix(format), arg1, arg2);
  }

  @Override
  public void error(String format, Object... arguments) {
    this.delegate.error(prefix(format), arguments);
  }

  @Override
  public void error(String msg, Throwable t) {
    this.delegate.error(prefix(msg), t);
  }

  @Override
  public boolean isErrorEnabled(Marker marker) {
    return this.delegate.isErrorEnabled(marker);
  }

  @Override
  public void error(Marker marker, String msg) {
    this.delegate.error(marker, prefix(msg));
  }

  @Override
  public void error(Marker marker, String format, Object arg) {
    this.delegate.error(marker, prefix(format), arg);
  }

  @Override
  public void error(Marker marker, String format, Object arg1, Object arg2) {
    this.delegate.error(marker, prefix(format), arg1, arg2);
  }

  @Override
  public void error(Marker marker, String format, Object... arguments) {
    this.delegate.error(marker, prefix(format), arguments);
  }

  @Override
  public void error(Marker marker, String msg, Throwable t) {
    this.delegate.error(marker, prefix(msg), t);
  }

}
