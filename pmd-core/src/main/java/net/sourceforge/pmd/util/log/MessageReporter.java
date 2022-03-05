/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.log;

import java.text.MessageFormat;

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

    boolean isLoggable(Level level);

    void log(Level level, String message, Object... formatArgs);

    void logEx(Level level, String message, Object[] formatArgs, Throwable error);

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

    default void error(String message, Object... formatArgs) {
        log(Level.ERROR, message, formatArgs);
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
