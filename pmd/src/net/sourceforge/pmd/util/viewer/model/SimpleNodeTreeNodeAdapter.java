package net.sourceforge.pmd.util.viewer.model;

import net.sourceforge.pmd.ast.SimpleNode;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;


/**
 * provides the adapter for the tree model
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class SimpleNodeTreeNodeAdapter
        implements TreeNode {
    private SimpleNode node;
    private List children;
    private SimpleNodeTreeNodeAdapter parent;

    /**
     * constructs the node
     *
     * @param node underlying AST's node
     */
    public SimpleNodeTreeNodeAdapter(SimpleNodeTreeNodeAdapter parent, SimpleNode node) {
        this.parent = parent;
        this.node = node;
    }

    /**
     * retrieves the underlying node
     *
     * @return AST node
     */
    public SimpleNode getSimpleNode() {
        return node;
    }

    /**
     * @see javax.swing.tree.TreeNode#getChildAt(int)
     */
    public TreeNode getChildAt(int childIndex) {
        checkChildren();

        return (TreeNode) children.get(childIndex);
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

        return children.size() == 0;
    }

    /**
     * @see javax.swing.tree.TreeNode#children()
     */
    public Enumeration children() {
        return Collections.enumeration(children);
    }

    /**
     * checks the children and creates them if neccessary
     */
    private void checkChildren() {
        if (children == null) {
            children = new ArrayList(node.jjtGetNumChildren());

            for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                children.add(new SimpleNodeTreeNodeAdapter(this, (SimpleNode) node.jjtGetChild(i)));
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


/*
 * $Log$
 * Revision 1.3  2004/09/27 19:42:52  tomcopeland
 * A ridiculously large checkin, but it's all just code reformatting.  Nothing to see here...
 *
 * Revision 1.2  2003/09/23 20:51:06  tomcopeland
 * Cleaned up imports
 *
 * Revision 1.1  2003/09/23 20:32:42  tomcopeland
 * Added Boris Gruschko's new AST/XPath viewer
 *
 * Revision 1.1  2003/09/24 01:33:03  bgr
 * moved to a new package
 *
 * Revision 1.1  2003/09/22 05:21:54  bgr
 * initial commit
 *
 */
