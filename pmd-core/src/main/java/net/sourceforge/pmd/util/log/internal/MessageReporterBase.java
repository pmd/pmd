/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.log.internal;

import static net.sourceforge.pmd.util.StringUtil.quoteMessageFormat;

import java.text.MessageFormat;
import java.util.Objects;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.event.Level;

import net.sourceforge.pmd.util.log.MessageReporter;

/**
 * Base implementation.
 *
 * @author Clément Fournier
 */
abstract class MessageReporterBase implements MessageReporter {

    private int numErrors;
    private @Nullable Level minLevel = Level.TRACE;

    /**
     * null level means off.
     */
    public final void setLevel(@Nullable Level minLevel) {
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
    public void logEx(Level level, @Nullable String message, Object[] formatArgs, @Nullable Throwable error) {
        if (isLoggable(level)) {
            if (error == null) {
                Objects.requireNonNull(message, "cannot call this method with null message and error");
                log(level, message, formatArgs);
                return;
            }
            if (level == Level.ERROR) {
                this.numErrors++;
            }
            String fullMessage = getErrorMessage(error);
            if (message != null) {
                message = MessageFormat.format(message, formatArgs);
                fullMessage = message + ": " + fullMessage;
            }
            logImpl(level, fullMessage);
            if (isLoggable(Level.DEBUG)) {
                String stackTrace = quoteMessageFormat(ExceptionUtils.getStackTrace(error));
                log(Level.DEBUG, stackTrace);
            }
        } else {
            // should be incremented even if not logged
            if (level == Level.ERROR) {
                this.numErrors++;
            }
        }
    }

    private @NonNull String getErrorMessage(Throwable error) {
        String errorMessage = error.getMessage();
        if (errorMessage == null) {
            errorMessage = error.getClass().getSimpleName();
        }
        return errorMessage;
    }

    @Override
    public final void log(Level level, String message, Object... formatArgs) {
        if (level == Level.ERROR) {
            this.numErrors++;
        }
        if (isLoggable(level)) {
            logImpl(level, MessageFormat.format(message, formatArgs));
        }
    }

    /**
     * Perform logging assuming {@link #isLoggable(Level)} is true.
     */
    protected abstract void logImpl(Level level, String message);


    @Override
    public int numErrors() {
        return numErrors;
    }
}
