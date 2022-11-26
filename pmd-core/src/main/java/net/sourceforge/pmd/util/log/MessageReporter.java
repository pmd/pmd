/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.log;

import java.text.MessageFormat;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.event.Level;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Façade to report user-facing messages (info, warning and error).
 * Note: messages are formatted using {@link MessageFormat}.
 *
 * <p>Internal API: this is a transitional API that will be significantly
 * changed in PMD 7, with the transition to SLF4J. See https://github.com/pmd/pmd/issues/3816
 *
 *  TODO rename to PmdReporter
 *
 * @author Clément Fournier
 */
@InternalApi
public interface MessageReporter {

    // todo change String to MessageFormat in those arg lists, it's too confusing
    // where to apply MessageFormat otherwise...

    boolean isLoggable(Level level);

    default void log(Level level, String message, Object... formatArgs) {
        logEx(level, message, formatArgs, null);
    }

    void logEx(Level level, @Nullable String message, Object[] formatArgs, @Nullable Throwable error);

    /**
     * Logs and returns a new exception.
     * Message and cause may not be null a the same time.
     */
    default RuntimeException newException(Level level, @Nullable Throwable cause, @Nullable String message, Object... formatArgs) {
        logEx(level, message, formatArgs, cause);
        if (message == null) {
            return new RuntimeException(cause);
        }
        return new RuntimeException(MessageFormat.format(message, formatArgs), cause);
    }

    default void info(String message, Object... formatArgs) {
        log(Level.INFO, message, formatArgs);
    }

    default void warn(String message, Object... formatArgs) {
        log(Level.WARN, message, formatArgs);
    }

    default void warnEx(String message, Throwable error) {
        logEx(Level.WARN, message, new Object[0], error);
    }

    default void warnEx(String message, Object[] formatArgs, Throwable error) {
        logEx(Level.WARN, message, formatArgs, error);
    }

    default RuntimeException error(String message, Object... formatArgs) {
        return error(null, message, formatArgs);
    }

    /**
     * Only one of the cause or the message can be null.
     */
    default RuntimeException error(@Nullable Throwable cause, @Nullable String contextMessage, Object... formatArgs) {
        return newException(Level.ERROR, cause, contextMessage, formatArgs);
    }

    default RuntimeException error(Throwable error) {
        return error(error, null);
    }

    default void errorEx(String message, Throwable error) {
        logEx(Level.ERROR, message, new Object[0], error);
    }

    default void errorEx(String message, Object[] formatArgs, Throwable error) {
        logEx(Level.ERROR, message, formatArgs, error);
    }

    /**
     * Returns the number of errors reported on this instance.
     * Any call to {@link #log(Level, String, Object...)} or
     * {@link #logEx(Level, String, Object[], Throwable)} with a level
     * of {@link Level#ERROR} should increment this number.
     */
    int numErrors();

}
