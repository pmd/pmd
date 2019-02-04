/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.app;

import org.reactfx.EventStream;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.XPathPanelController;
import net.sourceforge.pmd.util.fxdesigner.app.LogEntry.Category;
import net.sourceforge.pmd.util.fxdesigner.util.controls.AstTreeView;


/**
 * A control or controller that somehow displays nodes in a form that the user can select.
 * When a node is selected by the user (e.g. {@link AstTreeView}, {@link XPathPanelController}, etc),
 * the whole UI is synchronized to reflect information about the node. This includes scrolling
 * the TreeView, the editor, etc. To achieve that uniformly, node selection events are forwarded
 * as forwarded as messages on a {@link MessageChannel}.
 *
 * @author Clément Fournier
 */
public interface NodeSelectionSource extends ApplicationComponent {

    /** Channel used to transmit events to all interested components. */
    MessageChannel<Node> CHANNEL = new MessageChannel<>(Category.SELECTION_EVENT_TRACING);


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
    EventStream<Node> getSelectionEvents();


    /**
     * Updates the UI to react to a change in focus node. This is called whenever some selection source
     * in the tree records a change.
     */
    void setFocusNode(Node node);


    /**
     * Initialises this component. Must be called by the component somewhere.
     */
    default void initNodeSelectionHandling() {
        getSelectionEvents().subscribe(n -> CHANNEL.pushEvent(this, n));
        CHANNEL.messageStream(this).subscribe(this::setFocusNode);
    }

}
