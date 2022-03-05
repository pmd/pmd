/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.log.internal;

import java.text.MessageFormat;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;

import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.log.MessageReporter;

/**
 * A logger based on a {@link Logger}.
 *
 * @author Clément Fournier
 */
abstract class MessageReporterBase implements MessageReporter {

    private int numErrors;
    private Level minLevel = Level.TRACE;

    /**
     * null level means off.
     */
    public final void setLevel(Level minLevel) {
        this.minLevel = minLevel;
    }

    @Override
    public final boolean isLoggable(Level level) {
        return minLevel != null
            && minLevel.compareTo(level) >= 0
            && isLoggableImpl(level);
    }

    protected boolean isLoggableImpl(Level level) {
        return true;
    }

    @Override
    public void logEx(Level level, String message, Object[] formatArgs, Throwable error) {
        if (isLoggable(level)) {
            message = MessageFormat.format(message, formatArgs);
            String errorMessage = error.getMessage();
            if (errorMessage == null) {
                errorMessage = error.getClass().getSimpleName();
            }
            errorMessage = StringUtil.quoteMessageFormat(errorMessage);
            log(level, message + ": " + errorMessage);
            if (isLoggable(Level.DEBUG)) {
                String stackTrace = StringUtil.quoteMessageFormat(ExceptionUtils.getStackTrace(error));
                log(Level.DEBUG, stackTrace);
            }
        }
    }

    @Override
    public final void log(Level level, String message, Object... formatArgs) {
        if (level == Level.ERROR) {
            this.numErrors++;
        }
        if (isLoggable(level)) {
            logImpl(level, message, formatArgs);
        }
    }

    /**
     * Perform logging assuming {@link #isLoggable(Level)} is true.
     */
    protected abstract void logImpl(Level level, String message, Object[] formatArgs);

    @Override
    public void trace(String message, Object... formatArgs) {
        log(Level.TRACE, message, formatArgs);
    }

    @Override
    public void info(String message, Object... formatArgs) {
        log(Level.INFO, message, formatArgs);
    }

    @Override
    public void warn(String message, Object... formatArgs) {
        log(Level.WARN, message, formatArgs);
    }

    @Override
    public final void warnEx(String message, Throwable error) {
        warnEx(message, new Object[0], error);
    }

    @Override
    public void warnEx(String message, Object[] formatArgs, Throwable error) {
        logEx(Level.WARN, message, formatArgs, error);
    }

    @Override
    public void error(String message, Object... formatArgs) {
        log(Level.ERROR, message, formatArgs);
    }

    @Override
    public final void errorEx(String message, Throwable error) {
        errorEx(message, new Object[0], error);
    }

    @Override
    public void errorEx(String message, Object[] formatArgs, Throwable error) {
        logEx(Level.ERROR, message, formatArgs, error);
    }

    @Override
    public int numErrors() {
        return numErrors;
    }
}
