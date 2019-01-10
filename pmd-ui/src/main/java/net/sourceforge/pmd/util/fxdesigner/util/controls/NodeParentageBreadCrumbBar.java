/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.controls;

import java.util.function.Consumer;
import java.util.function.Function;

import org.controlsfx.control.BreadCrumbBar;
import org.reactfx.value.Val;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.util.IteratorUtil;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Region;
import javafx.util.Callback;


/**
 * @author Clément Fournier
 * @since 7.0.0
 */
public class NodeParentageBreadCrumbBar extends BreadCrumbBar<Node> {

    /** Special item used to truncate paths when they're too long. */
    private final TreeItem<Node> ellipsisCrumb = new TreeItem<>(null);



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
            button.setUserData(item);
            Val.wrap(button.focusedProperty())
               .values()
               .distinct()
               .filter(Boolean::booleanValue)
               // will change the node in the treeview on <- -> key presses
               .subscribe(b -> getOnCrumbAction().handle(new BreadCrumbActionEvent<>(item)));
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


        boolean found = false;

        // We're trying to estimate the ratio of px/crumb,
        // to make an educated guess about how many crumbs we can fit
        // in case we need to call setDeepestNode
        int totalNumChar = 0;
        int totalNumCrumbs = 0;
        // the sum of children width is the actual width with overflow
        double totalChildrenWidth = 0;

        for (javafx.scene.Node button : IteratorUtil.asReversed(getChildren())) {
            Node n = (Node) ((TreeItem<?>) button.getUserData()).getValue();

            // set the focus on the one being selected, remove on the others
            // calling requestFocus would switch the focus from eg the treeview to the crumb bar (unusable)
            button.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), n == node);

            // update counters
            totalNumChar += ((Labeled) button).getText().length();
            totalChildrenWidth += ((Region) button).getWidth();
            totalNumCrumbs++;

            if (n == node) {
                found = true;
            }
        }

        if (!found) {

            setDeepestNode(node, getWidthEstimator(totalNumChar, totalChildrenWidth, totalNumCrumbs));
            // set the deepest as focused
            Platform.runLater(() ->
                                  getChildren()
                                      .get(getChildren().size() - 1)
                                      .pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), true)
            );
        }
    }


    /**
     * Sets the given node to the selected (deepest) crumb. Parent crumbs are
     * added until they are estimated to overflow the visual space, after
     * which they are hidden into the ellipsis crumb.
     *
     * @param node           Node to set
     * @param widthEstimator Estimates the visual width of the crumb for one node
     */
    private void setDeepestNode(Node node, Function<Node, Double> widthEstimator) {
        TreeItem<Node> deepest = new TreeItem<>(node);
        TreeItem<Node> current = deepest;
        Node parent = node.jjtGetParent();
        double pathLength = widthEstimator.apply(node);

        final double maxPathLength = getWidth() - 150;

        while (parent != null && pathLength < maxPathLength) {
            TreeItem<Node> newItem = new TreeItem<>(parent);
            newItem.getChildren().add(current);
            current = newItem;
            pathLength += widthEstimator.apply(parent);
            parent = current.getValue().jjtGetParent();
        }

        if (pathLength >= maxPathLength
            // if parent == null then it's the root, no need for ellipsis
            && parent != null) {
            // the rest are children of the ellipsis
            ellipsisCrumb.getChildren().setAll(current);
        }

        setSelectedCrumb(deepest);
    }


    private static Function<Node, Double> getWidthEstimator(int totalNumDisplayedChars, double totalChildrenWidth, int totalNumCrumbs) {
        double pxByChar = totalNumDisplayedChars == 0
                          ? 5.0 // we have no data, too bad
                          // there's a ~19px constant padding per button (on my machine)
                          : (totalChildrenWidth - 19.0 * totalNumCrumbs) / totalNumDisplayedChars;

        return node -> node.getXPathNodeName().length() * pxByChar + 19.0;
    }

}
