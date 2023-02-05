/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.util.AssertionUtil;

/**
 * An initializer for {@link GlobalAnalysisListener} that gets notified of
 * general analysis parameters.
 * 
 * Each method will be called exactly once, before any events on the {@link GlobalAnalysisListener}
 */
public interface ListenerInitializer extends AutoCloseable {

    /**
     * Notifies the total number of files collected for analysis.
     */
    default void setNumberOfFilesToAnalyze(int totalFiles) {
        // noop
    }

    /**
     * Signals the end of initialization: no further calls will be made
     * to this object.
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
    static ListenerInitializer noop() {
        return NoopListenerInitializer.INSTANCE;
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
    static ListenerInitializer tee(Collection<? extends ListenerInitializer> listeners) {
        AssertionUtil.requireParamNotNull("Listeners", listeners);
        AssertionUtil.requireNotEmpty("Listeners", listeners);
        AssertionUtil.requireContainsNoNullValue("Listeners", listeners);

        List<ListenerInitializer> list = new ArrayList<>(listeners);
        list.removeIf(it -> it == NoopListenerInitializer.INSTANCE);

        if (list.isEmpty()) {
            return noop();
        } else if (list.size() == 1) {
            return list.iterator().next();
        }

        class TeeListener implements ListenerInitializer {

            @Override
            public void setNumberOfFilesToAnalyze(int totalFiles) {
                for (ListenerInitializer initializer : list) {
                    initializer.setNumberOfFilesToAnalyze(totalFiles);
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
