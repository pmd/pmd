/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.event.Level;

import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.log.MessageReporter;

/**
 * Reports errors that occur after parsing. This may be used to implement
 * semantic checks in a language specific way.
 */
public interface SemanticErrorReporter {
    // TODO use resource bundle keys instead of string messages.


    /**
     * Report an informational message at the given location.
     *
     * @param location   Location where the message should be reported
     * @param message    Message (rendered using a {@link MessageFormat})
     * @param formatArgs Format arguments
     */
    default void info(Node location, String message, Object... formatArgs) {
        // noop
    }


    /**
     * Report a warning at the given location. Warnings do not abort
     * the analysis.
     *
     * @param location   Location where the warning should be reported
     * @param message    Message (rendered using a {@link MessageFormat})
     * @param formatArgs Format arguments
     */
    void warning(Node location, String message, Object... formatArgs);


    /**
     * Report an error at the given location. Errors abort subsequent analysis.
     * The produced error can be thrown by the caller if it cannot be recovered
     * from.
     *
     * @param location   Location where the error should be reported
     * @param message    Message (rendered using a {@link MessageFormat})
     * @param formatArgs Format arguments
     */
    SemanticException error(Node location, String message, Object... formatArgs);


    /**
     * Returns true if at least one error has been reported.
     */
    boolean hasError();

    static SemanticErrorReporter noop() {
        return new SemanticErrorReporter() {

            private boolean hasError = false;

            @Override
            public void warning(Node location, String message, Object... formatArgs) {
                // noop
            }

            @Override
            public SemanticException error(Node location, String message, Object... formatArgs) {
                hasError = true;
                return new SemanticException(MessageFormat.format(message, formatArgs));
            }

            @Override
            public boolean hasError() {
                return hasError;
            }
        };
    }


    /**
     * Forwards to a {@link MessageReporter}, except trace and debug
     * messages which are reported on a logger.
     */
    static SemanticErrorReporter reportToLogger(MessageReporter reporter, Logger logger) {
        return new SemanticErrorReporter() {
            private boolean hasError = false;

            private String locPrefix(Node loc) {
                return "at " + loc.getReportLocation()
                    + ": ";
            }

            private String makeMessage(Node location, String message, Object[] args) {
                return locPrefix(location) + MessageFormat.format(message, args);
            }

            private String logMessage(Level level, Node location, String message, Object[] args) {
                String fullMessage = makeMessage(location, message, args);
                if (level.compareTo(Level.INFO) > 0) {
                    logger.atLevel(level).log(fullMessage);
                } else {
                    reporter.log(level, StringUtil.quoteMessageFormat(fullMessage)); // already formatted
                }
                return fullMessage;
            }

            @Override
            public void info(Node location, String message, Object... formatArgs) {
                logMessage(Level.INFO, location, message, formatArgs);
            }

            @Override
            public void warning(Node location, String message, Object... args) {
                logMessage(Level.WARN, location, message, args);
            }

            @Override
            public SemanticException error(Node location, String message, Object... args) {
                hasError = true;
                String fullMessage = logMessage(Level.ERROR, location, message, args);
                return new SemanticException(fullMessage);
            }

            @Override
            public boolean hasError() {
                return hasError;
            }
        };
    }

}
