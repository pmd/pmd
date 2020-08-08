/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.text.MessageFormat;

/**
 * Reports errors that occur after parsing. This may be used to implement
 * semantic checks in a language specific way.
 */
public interface SemanticErrorReporter {
    // TODO use resource bundle keys instead of string messages.
    // TODO do a real implementation, not just a noop


    /**
     * Report a warning at the given location. Warnings do not abort
     * the analysis.
     *
     * @param location   Location where the warning should be reported
     * @param message    Message (rendered using a {@link MessageFormat})
     * @param formatArgs Format arguments
     */
    void warn(Node location, String message, Object... formatArgs);


    /**
     * Report an error at the given location. Errors abort subsequent analysis.
     * The produced error can be thrown by the caller.
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
            public void warn(Node location, String message, Object... formatArgs) {
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


}
