/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.text.MessageFormat;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.event.Level;

import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.log.PmdReporter;

/**
 * Reports errors that occur after parsing. This may be used to implement
 * semantic checks in a language specific way.
 */
public interface SemanticErrorReporter {
    // TODO use resource bundle keys instead of string messages.


    /**
     * Report a warning at the given location. Warnings do not abort
     * the analysis. They are usually recoverable errors. They are used
     * to warn the user that something wrong is going on, which may cause
     * subsequent errors or inconsistent behavior.
     *
     * @param location   Location where the warning should be reported
     * @param message    Message (rendered using a {@link MessageFormat})
     * @param formatArgs Format arguments
     */
    void warning(Node location, String message, Object... formatArgs);


    /**
     * Report an error at the given location. Errors abort subsequent analysis
     * and cause a processing error to be put in the report. The produced error
     * can be thrown by the caller if it cannot be recovered from.
     *
     * @param location   Location where the error should be reported
     * @param message    Message (rendered using a {@link MessageFormat})
     * @param formatArgs Format arguments
     */
    SemanticException error(Node location, String message, Object... formatArgs);


    /**
     * If {@link #error(Node, String, Object...)} has been called, return
     * a semantic exception instance with the correct message. If it has been
     * called more than once, return the first exception, possibly with suppressed
     * exceptions for subsequent calls to {@link #error(Node, String, Object...)}.
     */
    @Nullable SemanticException getFirstError();

    static SemanticErrorReporter noop() {
        return new SemanticErrorReporter() {

            private SemanticException exception;

            @Override
            public void warning(Node location, String message, Object... formatArgs) {
                // noop
            }

            @Override
            public SemanticException error(Node location, String message, Object... formatArgs) {
                SemanticException ex = new SemanticException(MessageFormat.format(message, formatArgs));
                if (this.exception == null) {
                    this.exception = ex;
                } else {
                    this.exception.addSuppressed(ex);
                }
                return ex;
            }

            @Override
            public @Nullable SemanticException getFirstError() {
                return exception;
            }
        };
    }


    /**
     * Forwards to a {@link PmdReporter}, except trace and debug
     * messages which are reported on a logger.
     */
    static SemanticErrorReporter reportToLogger(PmdReporter reporter) {
        return new SemanticErrorReporter() {

            private SemanticException exception = null;

            private String locPrefix(Node loc) {
                return "at " + loc.getReportLocation().startPosToStringWithFile()
                    + ": ";
            }

            private String makeMessage(Node location, String message, Object[] args) {
                return locPrefix(location) + MessageFormat.format(message, args);
            }

            private String logMessage(Level level, Node location, String message, Object[] args) {
                String fullMessage = makeMessage(location, message, args);
                reporter.log(level, StringUtil.quoteMessageFormat(fullMessage)); // already formatted
                return fullMessage;
            }

            @Override
            public void warning(Node location, String message, Object... args) {
                logMessage(Level.DEBUG, location, message, args);
            }

            @Override
            public SemanticException error(Node location, String message, Object... args) {
                String fullMessage = logMessage(Level.ERROR, location, message, args);
                SemanticException ex = new SemanticException(fullMessage);
                if (this.exception == null) {
                    this.exception = ex;
                } else {
                    this.exception.addSuppressed(ex);
                }
                return ex;
            }

            @Override
            public @Nullable SemanticException getFirstError() {
                return exception;
            }
        };
    }

}
