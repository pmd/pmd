/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.viewer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.TreeNode;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * provides the adapter for the tree model
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
@Deprecated // to be removed with PMD 7.0.0
public class SimpleNodeTreeNodeAdapter implements TreeNode {

    private Node node;
    private List<TreeNode> children;
    private SimpleNodeTreeNodeAdapter parent;

    /**
     * constructs the node
     *
     * @param node
     *            underlying AST's node
     */
    public SimpleNodeTreeNodeAdapter(SimpleNodeTreeNodeAdapter parent, Node node) {
        this.parent = parent;
        this.node = node;
    }

    /**
     * retrieves the underlying node
     *
     * @return AST node
     */
    public Node getSimpleNode() {
        return node;
    }

    /**
     * @see javax.swing.tree.TreeNode#getChildAt(int)
     */
    @Override
    public TreeNode getChildAt(int childIndex) {
        checkChildren();
        return children.get(childIndex);
    }

    /**
     * @see javax.swing.tree.TreeNode#getChildCount()
     */
    @Override
    public int getChildCount() {
        checkChildren();
        return children.size();
    }

    /**
     * @see javax.swing.tree.TreeNode#getParent()
     */
    @Override
    public TreeNode getParent() {
        return parent;
    }

    /**
     * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
     */
    @Override
    public int getIndex(TreeNode node) {
        checkChildren();
        return children.indexOf(node);
    }

    /**
     * @see javax.swing.tree.TreeNode#getAllowsChildren()
     */
    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    /**
     * @see javax.swing.tree.TreeNode#isLeaf()
     */

    @Override
    public boolean isLeaf() {
        checkChildren();
        return children.isEmpty();
    }

    /**
     * @see javax.swing.tree.TreeNode#children()
     */

    @Override
    public Enumeration<TreeNode> children() {
        return Collections.enumeration(children);
    }

    /**
     * checks the children and creates them if neccessary
     */
    private void checkChildren() {
        if (children == null) {
            children = new ArrayList<>(node.getNumChildren());
            for (int i = 0; i < node.getNumChildren(); i++) {
                children.add(new SimpleNodeTreeNodeAdapter(this, node.getChild(i)));
            }
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return node.toString();
    }
}
