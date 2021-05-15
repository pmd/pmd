/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.exception.DefaultExceptionContext;
import org.apache.commons.lang3.exception.ExceptionContext;
import org.apache.commons.lang3.tuple.Pair;

/**
 * A {@link StackOverflowError} with nice messages.
 */
public final class ContextedStackOverflowError extends StackOverflowError implements ExceptionContext {

    private final ExceptionContext exceptionContext = new DefaultExceptionContext();

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
    public ContextedStackOverflowError addContextValue(String label, Object value) {
        exceptionContext.addContextValue(label, value);
        return this;
    }

    @Override
    public ContextedStackOverflowError setContextValue(String label, Object value) {
        exceptionContext.addContextValue(label, value);
        return this;
    }

    @Override
    public List<Object> getContextValues(String label) {
        return exceptionContext.getContextValues(label);
    }

    @Override
    public Object getFirstContextValue(String label) {
        return exceptionContext.getFirstContextValue(label);
    }

    @Override
    public Set<String> getContextLabels() {
        return exceptionContext.getContextLabels();
    }

    @Override
    public List<Pair<String, Object>> getContextEntries() {
        return exceptionContext.getContextEntries();
    }

    @Override
    public String getFormattedExceptionMessage(String baseMessage) {
        return exceptionContext.getFormattedExceptionMessage(baseMessage);
    }
}
