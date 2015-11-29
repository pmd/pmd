/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.viewer.model;


import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.sourceforge.pmd.lang.ast.Node;


/**
 * Model for the AST Panel Tree component
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */

public class ASTModel implements TreeModel {
	
    private Node root;
    private List<TreeModelListener> listeners = new ArrayList<>(1);

    /**
     * creates the tree model
     *
     * @param root tree's root
     */
    public ASTModel(Node root) {
        this.root = root;
    }

    /**
     * @see javax.swing.tree.TreeModel
     */
    public Object getChild(Object parent, int index) {
        return ((Node) parent).jjtGetChild(index);
    }

    /**
     * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
     */
    public int getChildCount(Object parent) {
        return ((Node) parent).jjtGetNumChildren();
    }

    /**
     * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object,java.lang.Object)
     */
    public int getIndexOfChild(Object parent, Object child) {
	Node node = (Node) parent;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            if (node.jjtGetChild(i).equals(child)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
     */
    public boolean isLeaf(Object node) {
        return ((Node) node).jjtGetNumChildren() == 0;
    }

    /**
     * @see javax.swing.tree.TreeModel#getRoot()
     */
    public Object getRoot() {
        return root;
    }

    /**
     * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath,java.lang.Object)
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
     */
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }


    /**
     * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
     */
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }


    protected void fireTreeModelEvent(TreeModelEvent e) {
        for (TreeModelListener listener : listeners) {
            listener.treeNodesChanged(e);
        }
    }

}

