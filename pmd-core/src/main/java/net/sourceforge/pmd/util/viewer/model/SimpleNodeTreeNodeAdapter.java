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

public class SimpleNodeTreeNodeAdapter implements TreeNode {
	
    private Node node;
    private List<TreeNode> children;
    private SimpleNodeTreeNodeAdapter parent;

    /**
     * constructs the node
     *
     * @param node underlying AST's node
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
    public TreeNode getChildAt(int childIndex) {
        checkChildren();
        return children.get(childIndex);
    }


    /**
     * @see javax.swing.tree.TreeNode#getChildCount()
     */
    public int getChildCount() {
        checkChildren();
        return children.size();
    }


    /**
     * @see javax.swing.tree.TreeNode#getParent()
     */
    public TreeNode getParent() {
        return parent;
    }

    /**
     * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
     */
    public int getIndex(TreeNode node) {
        checkChildren();
        return children.indexOf(node);
    }


    /**
     * @see javax.swing.tree.TreeNode#getAllowsChildren()
     */
    public boolean getAllowsChildren() {
        return true;
    }


    /**
     * @see javax.swing.tree.TreeNode#isLeaf()
     */

    public boolean isLeaf() {
        checkChildren();
        return children.isEmpty();
    }


    /**
     * @see javax.swing.tree.TreeNode#children()
     */

    public Enumeration<TreeNode> children() {
        return Collections.enumeration(children);
    }


    /**
     * checks the children and creates them if neccessary
     */
    private void checkChildren() {
        if (children == null) {
            children = new ArrayList<>(node.jjtGetNumChildren());
            for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                children.add(new SimpleNodeTreeNodeAdapter(this, node.jjtGetChild(i)));
            }
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return node.toString();
    }
}

