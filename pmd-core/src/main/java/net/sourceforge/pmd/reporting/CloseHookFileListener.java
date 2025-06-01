/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import net.sourceforge.pmd.reporting.Report.ProcessingError;
import net.sourceforge.pmd.reporting.Report.SuppressedViolation;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A delegating listener wrapper that can override the closing behavior.
 * @param <T> Type of the delegate
 *
 * @since 7.12.0
 */
public abstract class CloseHookFileListener<T extends FileAnalysisListener> implements FileAnalysisListener {
    private final T delegate;

    public CloseHookFileListener(T delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onRuleViolation(RuleViolation violation) {
        delegate.onRuleViolation(violation);
    }

    @Override
    public void onSuppressedRuleViolation(SuppressedViolation violation) {
        delegate.onSuppressedRuleViolation(violation);
    }

    @Override
    public void onError(ProcessingError error) {
        delegate.onError(error);
    }

    @Override
    public final void close() throws Exception {
        Exception e = null;
        try {
            delegate.close();
        } catch (Exception ex) {
            e = ex;
        }
        doClose(delegate, e);
    }

    @Override
    public String toString() {
        return "CloseHookFileListener [delegate=" + delegate + "]";
    }

    /**
     * Perform a close action. The delegate is given as a parameter, it has
     * already been closed.
     *
     * @param delegate The delegate
     * @param e Exception thrown by the delegate when it was closed, or null if it did not throw.
     *          The implementation can choose to rethrow that exception or suppress it.
     */
    protected abstract void doClose(T delegate, @Nullable Exception e) throws Exception;
}
