/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.log;

import java.text.MessageFormat;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.event.Level;

/**
 * Base implementation.
 *
 * @author Clément Fournier
 */
abstract class PmdLoggerBase implements PmdLogger {

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
            && minLevel.compareTo(level) <= 0
            && isLoggableImpl(level);
    }

    protected boolean isLoggableImpl(Level level) {
        return true;
    }

    @Override
    public void logEx(Level level, String message, Object[] formatArgs, Throwable error) {
        if (isLoggable(level)) {
            message = MessageFormat.format(message, formatArgs);
            log(level, message + ": " + error.getMessage());
            if (isLoggable(Level.DEBUG)) {
                log(Level.DEBUG, ExceptionUtils.getStackTrace(error));
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
    public int numErrors() {
        return numErrors;
    }
}
