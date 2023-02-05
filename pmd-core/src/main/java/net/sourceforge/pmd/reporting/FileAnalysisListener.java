/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.util.AssertionUtil;

/**
 * A handler for events occuring during analysis of a single file. Instances
 * are only used on a single thread for their entire lifetime, so don't
 * need to be synchronized to access state they own.
 *
 * <p>Listeners are assumed to be ready to receive events as soon as they
 * are constructed.
 */
public interface FileAnalysisListener extends AutoCloseable {


    /**
     * Handle a new violation (not suppressed).
     */
    void onRuleViolation(RuleViolation violation);


    /**
     * Handle a new suppressed violation.
     */
    default void onSuppressedRuleViolation(SuppressedViolation violation) {
        // by default do nothing
    }


    /**
     * Handle an error that occurred while processing a file.
     */
    default void onError(ProcessingError error) {
        // by default do nothing
    }


    /**
     * Signals the end of the analysis: no further calls will be made
     * to this listener. This is run in the thread the listener has
     * been used in. This means, if this routine merges some state
     * into some global state of the {@link GlobalAnalysisListener),
     * then that must be synchronized.
     *
     * @throws Exception If an exception occurs, eg IOException when writing to a renderer
     */
    @Override
    default void close() throws Exception {
        // by default do nothing
    }


    /**
     * A listener that does nothing.
     */
    static FileAnalysisListener noop() {
        return NoopFileListener.INSTANCE;
    }


    /**
     * Produce an analysis listener that forwards all events to the given
     * listeners.
     *
     * @param listeners Listeners
     *
     * @return A new listener
     *
     * @throws IllegalArgumentException If the parameter is empty
     * @throws NullPointerException     If the parameter or any of its elements is null
     */
    @SuppressWarnings("PMD.CloseResource")
    static FileAnalysisListener tee(Collection<? extends FileAnalysisListener> listeners) {
        AssertionUtil.requireParamNotNull("Listeners", listeners);
        AssertionUtil.requireNotEmpty("Listeners", listeners);
        AssertionUtil.requireContainsNoNullValue("Listeners", listeners);

        List<FileAnalysisListener> list = new ArrayList<>(listeners);
        list.removeIf(it -> it == NoopFileListener.INSTANCE);

        if (list.isEmpty()) {
            return noop();
        } else if (list.size() == 1) {
            return list.iterator().next();
        }

        class TeeListener implements FileAnalysisListener {

            @Override
            public void onRuleViolation(RuleViolation violation) {
                for (FileAnalysisListener it : list) {
                    it.onRuleViolation(violation);
                }
            }

            @Override
            public void onSuppressedRuleViolation(SuppressedViolation violation) {
                for (FileAnalysisListener it : list) {
                    it.onSuppressedRuleViolation(violation);
                }
            }

            @Override
            public void onError(ProcessingError error) {
                for (FileAnalysisListener it : list) {
                    it.onError(error);
                }
            }

            @Override
            public void close() throws Exception {
                Exception composed = IOUtil.closeAll(list);
                if (composed != null) {
                    throw composed;
                }
            }

            @Override
            public String toString() {
                return "Tee" + list;
            }
        }

        return new TeeListener();
    }

}
