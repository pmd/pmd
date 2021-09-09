/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.exception.ExceptionContext;
import org.apache.commons.lang3.tuple.Pair;

public interface ExceptionContextDefaultImpl<T extends Throwable & ExceptionContext> extends ExceptionContext {

    ExceptionContext getExceptionContext();

    T getThrowable();

    @Override
    default T addContextValue(String label, Object value) {
        getExceptionContext().addContextValue(label, value);
        return getThrowable();
    }

    @Override
    default ExceptionContext setContextValue(String label, Object value) {
        return getExceptionContext().addContextValue(label, value);
    }

    @Override
    default List<Object> getContextValues(String label) {
        return getExceptionContext().getContextValues(label);
    }

    @Override
    default Object getFirstContextValue(String label) {
        return getExceptionContext().getFirstContextValue(label);
    }

    @Override
    default Set<String> getContextLabels() {
        return getExceptionContext().getContextLabels();
    }

    @Override
    default List<Pair<String, Object>> getContextEntries() {
        return getExceptionContext().getContextEntries();
    }

    @Override
    default String getFormattedExceptionMessage(String baseMessage) {
        return getExceptionContext().getFormattedExceptionMessage(baseMessage);
    }
}
