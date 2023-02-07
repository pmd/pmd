/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import org.apache.commons.lang3.exception.DefaultExceptionContext;

import net.sourceforge.pmd.internal.util.ExceptionContextDefaultImpl;

/**
 * A {@link StackOverflowError} with nice messages.
 */
public final class ContextedStackOverflowError extends StackOverflowError implements ExceptionContextDefaultImpl<ContextedStackOverflowError> {
    /** The serialization version. */
    private static final long serialVersionUID = 4111035582093848670L;

    private final DefaultExceptionContext exceptionContext = new DefaultExceptionContext();

    private ContextedStackOverflowError(StackOverflowError e) {
        super(e.getMessage());
        setStackTrace(e.getStackTrace()); // pretend we're a regular assertion error
    }


    public static ContextedStackOverflowError wrap(StackOverflowError e) {
        return e instanceof ContextedStackOverflowError ? (ContextedStackOverflowError) e
                                                        : new ContextedStackOverflowError(e);
    }

    @Override
    public String getMessage() {
        return getFormattedExceptionMessage(super.getMessage());
    }

    @Override
    public DefaultExceptionContext getExceptionContext() {
        return exceptionContext;
    }

    @Override
    public ContextedStackOverflowError getThrowable() {
        return this;
    }
}
