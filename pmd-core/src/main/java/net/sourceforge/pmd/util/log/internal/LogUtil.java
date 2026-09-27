/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.log.internal;

import org.slf4j.Logger;

public final class LogUtil {
    private LogUtil() {}

    public interface WarnOrDebugLogger {
        void log(Logger logger, String message, Object... arguments);
    }

    /**
     * Depending on the flag {@code shouldWarn} the message (with arguments) is either
     * logged at level warn or otherwise at level debug.
     *
     * <p>When logged at level warn, the hint is appended. The hint explains how to
     * disable the logging.</p>
     *
     * <p>When there is a throwable provided, the stacktrace is only logged at
     * debug in any case.</p>
     */
    public static WarnOrDebugLogger createWarnOrDebugLogger(boolean shouldWarn, String hint) {
        return (logger, message, arguments) -> {
            if (shouldWarn) {
                Throwable throwable = null;
                Object[] argumentsWithoutThrowable = arguments;
                int lastArgumentIndex = arguments.length - 1;
                if (arguments[lastArgumentIndex] instanceof Throwable) {
                    throwable = (Throwable) arguments[lastArgumentIndex];
                    argumentsWithoutThrowable = new Object[lastArgumentIndex];
                    System.arraycopy(arguments, 0, argumentsWithoutThrowable, 0, argumentsWithoutThrowable.length);
                }
                logger.warn(message + " " + hint, argumentsWithoutThrowable);
                if (throwable != null) {
                    logger.debug("Exception occurred", throwable);
                }
            } else {
                logger.debug(message, arguments);
            }
        };
    }
}
