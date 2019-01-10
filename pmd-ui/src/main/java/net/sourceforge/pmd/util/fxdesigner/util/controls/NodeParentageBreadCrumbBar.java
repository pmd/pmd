/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.controls;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

import org.controlsfx.control.BreadCrumbBar;

import net.sourceforge.pmd.lang.ast.Node;

import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.util.Callback;


/**
 * @author Clément Fournier
 * @since 7.0.0
 */
public class NodeParentageBreadCrumbBar extends BreadCrumbBar<Node> {

    private static final int MAX_PATH_LENGTH_BEFORE_ELLIPSIS = 300;
    /** Special item used to truncate paths when they're too long. */
    private final TreeItem<Node> ellipsisCrumb = new TreeItem<>(null);

    private final Map<Node, Button> nodeToDisplayedButton = new WeakHashMap<>();

    // Current number of crumbs being displayed (discounting the ellipsis)
    private int currentDisplayedNodes = 0;


    public NodeParentageBreadCrumbBar() {
        // This allows to click on a parent crumb and keep the children crumb
        setAutoNavigationEnabled(false);

        // captured in the closure
        final Callback<TreeItem<Node>, Button> originalCrumbFactory = getCrumbFactory();

        setCrumbFactory(item -> {
            Button button = originalCrumbFactory.call(item);
            if (item == ellipsisCrumb) {
                button.setText("...");
            }
            nodeToDisplayedButton.put(item.getValue(), button);
            button.setUserData(item);
            return button;
        });


    }


    /**
     * Set a handler that executes when the user selects a crumb other than the ellipsis.
     * This shouldn't be calling {@link #setFocusNode(Node)} on the same node otherwise
     * the crumb bar will set the deepest node to the node and the children won't be
     * available.
     */
    public void setOnRegularCrumbAction(Consumer<TreeItem<Node>> handler) {
        setOnCrumbAction(e -> {
            if (e.getSelectedCrumb() != ellipsisCrumb) {
                handler.accept(e.getSelectedCrumb());
            }
        });
    }

    // getSelectedCrumb gets the deepest displayed node


    /**
     * If the node is already displayed on the crumbbar, only
     * sets the focus on it. Otherwise, sets the node to be
     * the deepest one of the crumb bar.
     */
    public void setFocusNode(Node node) {

        Iterable<Button> children = () -> getChildren().stream().map(c -> (Button) c).iterator();

        boolean found = false;
        for (Button button : children) {
            Node n = (Node) ((TreeItem<?>) button.getUserData()).getValue();
            // set the focus on the one being selected
            button.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), n == node);
            if (n == node) {
                found = true;
            }
        }
        if (!found) {
            setDeepestNode(node);
        }
    }


    private void setDeepestNode(Node node) {
        TreeItem<Node> deepest = new TreeItem<>(node);
        TreeItem<Node> current = deepest;
        Node parent = node.jjtGetParent();
        int pathLength = getBreadCrumbLengthEstimate(node);
        int pathLengthInNodes = 1;

        while (parent != null && pathLength < MAX_PATH_LENGTH_BEFORE_ELLIPSIS) {
            TreeItem<Node> newItem = new TreeItem<>(parent);
            newItem.getChildren().add(current);
            current = newItem;
            pathLength += getBreadCrumbLengthEstimate(parent);
            parent = current.getValue().jjtGetParent();
            pathLengthInNodes++;
        }

        if (pathLength >= MAX_PATH_LENGTH_BEFORE_ELLIPSIS) {
            // the rest are children of the ellipsis
            ellipsisCrumb.getChildren().setAll(current);
        }

        currentDisplayedNodes = pathLengthInNodes;

        setSelectedCrumb(deepest);
    }


    // This is arbitrary
    private int getBreadCrumbLengthEstimate(Node n) {
        return n.getXPathNodeName().length() + 10/* separator constant */;
    }

    //
    //    private static class BreadCrumbPathLengthEstimator<T> {
    //        private final BreadCrumbBar<T> lengthEstimationCrumbBar = new BreadCrumbBar<>();
    //        private final Scene dummyScene;
    //        private final Supplier<Integer> maxWidthSupplier;
    //
    //        private TreeItem<T> currentTop;
    //
    //
    //        BreadCrumbPathLengthEstimator(List<String> styleSheets, Supplier<Integer> maxWidthSupplier) {
    //            this.maxWidthSupplier = maxWidthSupplier;
    //            AnchorPane dummyPane = new AnchorPane();
    //            dummyPane.getChildren().add(lengthEstimationCrumbBar);
    //
    //            Scene scene = new Scene(dummyPane);
    //            scene.getStylesheets().addAll(styleSheets);
    //            dummyScene = scene;
    //        }
    //
    //
    //        boolean tryAddNode(T n) {
    //            TreeItem<T> item = new TreeItem<>(n);
    //
    //            if (currentTop != null) {
    //                item.getChildren().add(currentTop);
    //                currentTop = item;
    //            }
    //
    //            lengthEstimationCrumbBar.setSelectedCrumb(item);
    //
    //            // TODO
    //        }
    //    }

}
