/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.log;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.internal.util.AssertionUtil;

/**
 * Logger façade. Can probably be converted to just SLF4J logger in PMD 7.
 *
 * @author Clément Fournier
 */
@InternalApi
public interface PmdLogger {

    boolean isLoggable(Level level);

    void log(Level level, String message, Object... formatArgs);

    void logEx(Level level, String message, Object[] formatArgs, Throwable error);

    void info(String message, Object... formatArgs);

    void trace(String message, Object... formatArgs);

    void debug(String message, Object... formatArgs);

    void warning(String message, Object... formatArgs);

    void warningEx(String message, Throwable error);

    void warningEx(String message, Object[] formatArgs, Throwable error);

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

    // levels, in sync with SLF4J levels
    enum Level {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR;

        java.util.logging.Level toJutilLevel() {
            switch (this) {
            case DEBUG:
                return java.util.logging.Level.FINE;
            case ERROR:
                return java.util.logging.Level.SEVERE;
            case INFO:
                return java.util.logging.Level.INFO;
            case TRACE:
                return java.util.logging.Level.FINER;
            case WARN:
                return java.util.logging.Level.WARNING;
            default:
                throw AssertionUtil.shouldNotReachHere("exhaustive");
            }
        }
    }

}
