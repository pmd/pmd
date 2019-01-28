/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util;

import org.reactfx.EventStream;
import org.reactfx.EventStreams;

import net.sourceforge.pmd.lang.ast.Node;

import javafx.collections.ObservableSet;


/**
 * @author Clément Fournier
 */
public interface CompositeSelectionSource extends NodeSelectionSource {


    ObservableSet<? extends NodeSelectionSource> getComponents();


    @Override
    default EventStream<NodeSelectionEvent> getSelectionEvents() {
        return EventStreams.merge(getComponents(), NodeSelectionSource::getSelectionEvents);
    }


    @Override
    default void setFocusNode(Node node) {
        // by default do nothing,
        // maybe it should only be handled by the components
    }


    @Override
    default void select(NodeSelectionEvent selectionEvent) {
        NodeSelectionSource.super.select(selectionEvent);

        for (NodeSelectionSource source : getComponents()) {
            logSelectionEventTrace(selectionEvent, () -> getDebugName() + " forwards to " + source.getDebugName());
            source.select(selectionEvent);
        }
    }
}
