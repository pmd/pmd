/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.controls;

import static net.sourceforge.pmd.util.fxdesigner.util.DesignerIteratorUtil.parentIterator;
import static net.sourceforge.pmd.util.fxdesigner.util.DesignerIteratorUtil.reverse;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.reactfx.value.Var;

import net.sourceforge.pmd.lang.ast.Node;

import javafx.scene.control.TreeItem;

/**
 * Represents a tree item (data, not UI) in the ast TreeView.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public final class ASTTreeItem extends TreeItem<Node> {


    private Var<ASTTreeCell> treeCell = Var.newSimpleVar(null);

    /**
     * Latent style classes are style classes that logically belong to this tree item (i.e. the node it wraps).
     * The TreeItem must sync them to the TreeCell that currently displays it. The value is never null.
     */
    private Var<List<String>> latentStyleClasses = Var.newSimpleVar(Collections.emptyList());

    private ASTTreeItem(Node n) {
        super(n);
        setExpanded(true);

        treeCellProperty().changes().subscribe(change -> {

            if (change.getOldValue() != null) {
                change.getOldValue().getStyleClass().removeAll(latentStyleClasses.getValue());
            }

            if (change.getNewValue() != null) {
                change.getNewValue().getStyleClass().addAll(latentStyleClasses.getValue());
            }

        });

        latentStyleClasses.changes()
                          // .conditionOn(treeCellProperty().map(Objects::nonNull))
                          .subscribe(change -> {
                              if (treeCellProperty().isPresent()) {
                                  treeCellProperty().getValue().getStyleClass().removeAll(change.getOldValue());
                                  treeCellProperty().getValue().getStyleClass().addAll(change.getNewValue());
                              }
                          });
    }


    /**
     * Finds the tree item corresponding to the given node
     * among the descendants of this item. This method assumes
     * this item is the root node.
     *
     * @param node The node to find
     *
     * @return The found item, or null if this item doesn't wrap the
     *         root of the tree to which the parameter belongs
     */
    public ASTTreeItem findItem(Node node) {
        // This is an improvement over the previous algorithm which performed a greedy
        // depth-first traversal over all the tree (was at worst O(size of the tree),
        // now it's at worst O(number of parents of the searched node))

        Objects.requireNonNull(node, "Cannot find a null item");

        Iterator<Node> pathToNode = reverse(parentIterator(node, true));

        if (pathToNode.next() != getValue()) {
            // this node is not the root of the tree
            // to which the node we're looking for belongs
            return null;
        }

        ASTTreeItem current = this;

        while (pathToNode.hasNext()) {
            Node currentNode = pathToNode.next();

            current = current.getChildren().stream()
                             .filter(item -> item.getValue() == currentNode)
                             .findAny()
                             .map(ASTTreeItem.class::cast)
                             .get(); // theoretically, this cannot fail, since we use reference identity

        }

        return current;
    }


    /** Builds an ASTTreeItem recursively from a node. */
    public static ASTTreeItem getRoot(Node n) {
        ASTTreeItem item = new ASTTreeItem(n);
        if (n.jjtGetNumChildren() > 0) {
            for (int i = 0; i < n.jjtGetNumChildren(); i++) {
                item.getChildren().add(getRoot(n.jjtGetChild(i)));
            }
        }
        return item;
    }


    public void setStyleClasses(List<String> classes) {
        latentStyleClasses.setValue(classes == null ? Collections.emptyList() : classes);
    }


    public void setStyleClasses(String... classes) {
        setStyleClasses(Arrays.asList(classes));
    }


    // Only for ASTTreeCell
    Var<ASTTreeCell> treeCellProperty() {
        return treeCell;
    }


}
