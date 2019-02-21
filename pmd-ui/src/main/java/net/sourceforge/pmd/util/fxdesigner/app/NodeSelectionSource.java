/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.app;

import org.reactfx.EventStream;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.XPathPanelController;
import net.sourceforge.pmd.util.fxdesigner.util.controls.AstTreeView;


/**
 * A control or controller that somehow displays nodes in a form that the user can select.
 * When a node is selected by the user (e.g. {@link AstTreeView}, {@link XPathPanelController}, etc),
 * the whole UI is synchronized to reflect information about the node. This includes scrolling
 * the TreeView, the editor, etc. To achieve that uniformly, node selection events are forwarded
 * as messages on a {@link MessageChannel}.
 *
 * @author Clément Fournier
 */
public interface NodeSelectionSource extends ApplicationComponent {


    /**
     * Updates the UI to react to a change in focus node. This is called whenever some selection source
     * in the tree records a change.
     */
    void setFocusNode(Node node);


    /**
     * Initialises this component. Must be called by the component somewhere.
     *
     * @param root                  Instance of the app. Should be the same as {@link #getDesignerRoot()},
     *                              but the parameter here is to make it clear that {@link #getDesignerRoot()}
     *                              must be initialized before this method is called.
     * @param mySelectionEvents     Stream of nodes that should push an event each
     * @param alwaysHandleSelection Whether the component should handle selection events that originated from itself.
     *                              For now some must, because they aggregate several selection sources (the {@link net.sourceforge.pmd.util.fxdesigner.NodeInfoPanelController}).
     *                              Splitting it into separate controls will remove the need for that.
     */
    default void initNodeSelectionHandling(DesignerRoot root,
                                           EventStream<? extends Node> mySelectionEvents,
                                           boolean alwaysHandleSelection) {
        MessageChannel<Node> channel = root.getNodeSelectionChannel();
        mySelectionEvents.subscribe(n -> channel.pushEvent(this, n));
        channel.messageStream(alwaysHandleSelection, this).subscribe(this::setFocusNode);
    }

}
