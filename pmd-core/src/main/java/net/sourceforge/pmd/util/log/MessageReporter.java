/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.log;

import java.text.MessageFormat;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.internal.util.AssertionUtil;

/**
 * Façade to report user-facing messages (info, warning and error).
 * Note: messages are formatted using {@link MessageFormat}.
 *
 * <p>Internal API: this is a transitional API that will be significantly
 * changed in PMD 7, with the transition to SLF4J. See https://github.com/pmd/pmd/issues/3816
 *
 * @author Clément Fournier
 */
@InternalApi
public interface MessageReporter {

    boolean isLoggable(Level level);

    void log(Level level, String message, Object... formatArgs);

    void logEx(Level level, String message, Object[] formatArgs, Throwable error);

    void info(String message, Object... formatArgs);

    /**
     * @deprecated Trace messages should be reported on a Logger instance.
     * This is kept because it's simpler to port calls to this method to
     * SLF4J in PMD 7 than to do the same if these calls were calls to a
     * java.util.logging.Logger in PMD 6.
     */
    @Deprecated
    void trace(String message, Object... formatArgs);

    void warn(String message, Object... formatArgs);

    void warnEx(String message, Throwable error);

    void warnEx(String message, Object[] formatArgs, Throwable error);

    void error(String message, Object... formatArgs);

    void errorEx(String message, Throwable error);

    void errorEx(String message, Object[] formatArgs, Throwable error);

    /**
     * Returns the number of errors reported on this instance.
     * Any call to {@link #log(Level, String, Object...)} or
     * {@link #logEx(Level, String, Object[], Throwable)} with a level
     * of {@link Level#ERROR} should increment this number.
     */
    int numErrors();

    /**
     * Message severity level. This maps to SLF4J levels transparently.
     *
     * @deprecated Will be replaced with SLF4J Level in PMD 7
     */
    @Deprecated
    enum Level {
        ERROR,
        WARN,
        INFO,
        DEBUG,
        TRACE;

        public java.util.logging.Level toJutilLevel() {
            switch (this) {
            case ERROR:
                return java.util.logging.Level.SEVERE;
            case WARN:
                return java.util.logging.Level.WARNING;
            case INFO:
                return java.util.logging.Level.INFO;
            case DEBUG:
                return java.util.logging.Level.FINE;
            case TRACE:
                return java.util.logging.Level.FINER;
            default:
                throw AssertionUtil.shouldNotReachHere("exhaustive");
            }
        }
    }

}
