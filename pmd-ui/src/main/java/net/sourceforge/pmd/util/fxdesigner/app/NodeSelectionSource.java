/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.app;

import java.util.Objects;

import org.reactfx.EventStream;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.MainDesignerController;
import net.sourceforge.pmd.util.fxdesigner.XPathPanelController;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsOwner;
import net.sourceforge.pmd.util.fxdesigner.util.controls.AstTreeView;


/**
 * A control or controller that somehow displays nodes in a form that the user can select.
 * When a node is selected by the user (e.g. {@link AstTreeView}, {@link XPathPanelController}, etc),
 * the whole UI is synchronized to reflect information about the node. This includes scrolling
 * the TreeView, the editor, etc. To achieve that uniformly, node selection events are merged
 * into a global stream for the whole app. Events from that stream are handled by {@link MainDesignerController}.
 *
 * <p>Node selection sources form a tree parallel to {@link AbstractController} and {@link SettingsOwner}.
 * This interface implements behaviour for leaves of the tree. Inner nodes are handled by
 * {@link CompositeSelectionSource}.
 *
 * @author Clément Fournier
 */
public interface NodeSelectionSource extends ApplicationComponent {

    /**
     * Returns a stream of events that should push an event each time
     * this source or one of its sub components records a change in node
     * selection. This one needs to be implemented in sub classes.
     *
     * <p>You can't trust that this method will return the same stream
     * when called several times. In fact it's just called one time.
     * That's why you can't abstract the suppressible behaviour here.
     * You'd need Scala traits.
     */
    EventStream<NodeSelectionEvent> getSelectionEvents();


    /**
     * Bubbles a selection event down the tree. First, {@link #setFocusNode(Node)} is called to
     * handle the event (if the event didn't originate from here). If this is not a leaf of the tree,
     * then the event is forwarded to the children nodes as well.
     *
     * @param selectionEvent Event to handle
     */
    default void bubbleDown(NodeSelectionEvent selectionEvent) {
        if (alwaysHandleSelection() || selectionEvent.getOrigin() != this) {
            logSelectionEventTrace(selectionEvent, () -> "\t" + this.getDebugName() + " is handling event");
            setFocusNode(selectionEvent.getSelection());
        }
    }


    /**
     * Updates the UI to react to a change in focus node. This is called whenever some selection source
     * in the tree records a change. The event is not forwarded to its origin unless {@link #alwaysHandleSelection()}
     * is overridden to return true.
     */
    void setFocusNode(Node node);


    /** Whether to also handle events which originated from this controller. */
    default boolean alwaysHandleSelection() {
        return false;
    }


    /**
     * An event fired when the user selects a node somewhere in the UI
     * and bubbled up to the {@link MainDesignerController}.
     */
    final class NodeSelectionEvent {

        private final Node selection;
        private final NodeSelectionSource origin;


        public NodeSelectionEvent(Node selection, NodeSelectionSource origin) {
            this.selection = selection;
            this.origin = origin;
        }


        public Node getSelection() {
            return selection;
        }


        public NodeSelectionSource getOrigin() {
            return origin;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            NodeSelectionEvent that = (NodeSelectionEvent) o;
            return Objects.equals(selection, that.selection) &&
                Objects.equals(origin, that.origin);
        }


        @Override
        public int hashCode() {
            return Objects.hash(selection, origin);
        }


        @Override
        public String toString() {
            return getSelection().getXPathNodeName() + "(" + hashCode() + ") from " + getOrigin().getClass().getSimpleName();
        }
    }

}
