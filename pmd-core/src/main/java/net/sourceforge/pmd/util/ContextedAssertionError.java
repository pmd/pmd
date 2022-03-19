/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import org.apache.commons.lang3.exception.DefaultExceptionContext;

import net.sourceforge.pmd.internal.util.ExceptionContextDefaultImpl;

/**
 * An {@link AssertionError} with nice messages.
 */
public final class ContextedAssertionError extends AssertionError implements ExceptionContextDefaultImpl<ContextedAssertionError> {
    /** The serialization version. */
    private static final long serialVersionUID = -8919808081157463410L;

    private final DefaultExceptionContext exceptionContext = new DefaultExceptionContext();

    private ContextedAssertionError(AssertionError e) {
        super(e.getMessage());
        setStackTrace(e.getStackTrace()); // pretend we're a regular assertion error
    }


    public static ContextedAssertionError wrap(AssertionError e) {
        return e instanceof ContextedAssertionError ? (ContextedAssertionError) e
                                                    : new ContextedAssertionError(e);
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
    public ContextedAssertionError getThrowable() {
        return this;
    }
}
