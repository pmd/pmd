/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.app;

import org.reactfx.EventStream;
import org.reactfx.EventStreams;

import net.sourceforge.pmd.lang.ast.Node;

import javafx.collections.ObservableSet;


/**
 * A {@link NodeSelectionSource} that merges the events of several sub-components. Such a source
 * can also handle events itself via {@link #setFocusNode(Node)}.
 *
 * @author Clément Fournier
 */
public interface CompositeSelectionSource extends NodeSelectionSource {

    /** Returns the sources to forward to when bubbling down, and from which to merge events when bubbling up. */
    ObservableSet<? extends NodeSelectionSource> getSubSelectionSources();


    @Override
    default EventStream<NodeSelectionEvent> getSelectionEvents() {
        return EventStreams.merge(getSubSelectionSources(), NodeSelectionSource::getSelectionEvents);
    }


    @Override
    default void setFocusNode(Node node) {
        // by default do nothing,
        // maybe it should only be handled by the components
    }


    @Override
    default void bubbleDown(NodeSelectionEvent selectionEvent) {
        NodeSelectionSource.super.bubbleDown(selectionEvent);

        for (NodeSelectionSource source : getSubSelectionSources()) {
            logSelectionEventTrace(selectionEvent, () -> getDebugName() + " forwards to " + source.getDebugName());
            source.bubbleDown(selectionEvent);
        }
    }
}
