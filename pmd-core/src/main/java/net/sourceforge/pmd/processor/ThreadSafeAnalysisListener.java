/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.RuleViolation;

/**
 * A handler for analysis events. This must be thread safe.
 */
public interface ThreadSafeAnalysisListener extends AutoCloseable {


    /**
     * Handle a new violation (not suppressed).
     */
    default void onRuleViolation(RuleViolation violation) {

    }


    /**
     * Handle a new suppressed violation.
     */
    default void onSuppressedRuleViolation(SuppressedViolation violation) {

    }


    /**
     * Handle an error that occurred while processing a file.
     */
    default void onError(ProcessingError error) {

    }


    @Override
    default void close() throws Exception {

    }


    static ThreadSafeAnalysisListener noop() {
        return new ThreadSafeAnalysisListener() {};
    }


    static ThreadSafeAnalysisListener tee(Collection<? extends ThreadSafeAnalysisListener> listeners) {
        List<ThreadSafeAnalysisListener> list = Collections.unmodifiableList(new ArrayList<>(listeners));
        return new ThreadSafeAnalysisListener() {
            @Override
            public void onRuleViolation(RuleViolation violation) {
                for (ThreadSafeAnalysisListener it : list) {
                    it.onRuleViolation(violation);
                }
            }

            @Override
            public void onSuppressedRuleViolation(SuppressedViolation violation) {
                for (ThreadSafeAnalysisListener it : list) {
                    it.onSuppressedRuleViolation(violation);
                }
            }

            @Override
            public void onError(ProcessingError error) {
                for (ThreadSafeAnalysisListener it : list) {
                    it.onError(error);
                }
            }

            @Override
            public void close() throws Exception {
                Exception composed = null;
                for (ThreadSafeAnalysisListener it : list) {
                    try {
                        it.close();
                    } catch (Exception e) {
                        if (composed == null) {
                            composed = e;
                        } else {
                            composed.addSuppressed(e);
                        }
                    }
                }
                if (composed != null) {
                    throw composed;
                }
            }
        };

    }

}
