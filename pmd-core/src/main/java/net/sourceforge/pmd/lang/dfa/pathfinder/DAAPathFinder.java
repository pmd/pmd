/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.dfa.pathfinder;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.dfa.NodeType;

/**
 *         Finds all paths of a data flow. Each loop will be 0 or 2 times traversed ->
 *         2 paths. This is special to the data flow anomaly analysis.
 * @since Created on 09.08.2004
 * @author raik
 */
public class DAAPathFinder {
    private static final int MAX_PATHS = 5000;

    /** Maximum loops to prevent hanging of PMD. See https://sourceforge.net/p/pmd/bugs/1393/ */
    private static final int MAX_LOOPS = 100;

    private DataFlowNode rootNode;
    private Executable shim;
    private CurrentPath currentPath = new CurrentPath();
    private DefaultMutableTreeNode stack = new DefaultMutableTreeNode();
    private int maxPaths;

    public DAAPathFinder(DataFlowNode rootNode, Executable shim) {
        this.rootNode = rootNode;
        this.shim = shim;
        this.maxPaths = MAX_PATHS;
    }
    
    public DAAPathFinder(DataFlowNode rootNode, Executable shim, int maxPaths) {
        this.rootNode = rootNode;
        this.shim = shim;
        this.maxPaths = maxPaths;
    }

    public void run() {
        phase1();
    }

    /*
     * Initialise the path search. Starts the searching.
     * */
    private void phase1() {
        currentPath.addLast(rootNode);
        int i = 0;
        boolean flag = true;
        do {
            i++;
//            System.out.println("Building path from " + currentPath.getLast());
            phase2(flag);
            shim.execute(currentPath);
            flag = false;
        } while (i < maxPaths && phase3());
    }

    /*
     * Builds up the path.
     * */
    private void phase2(boolean flag) {
        int i = 0;
        while (!currentPath.isEndNode() && i < MAX_LOOPS) {
            i++;
            if (currentPath.isBranch() || currentPath.isFirstDoStatement()) {
                if (flag) {
                    addNodeToTree();
                }
                flag = true;
                if (countLoops() <= 2) {
                    addCurrentChild();
                    continue;
                } else {
                    // jump out of that loop
                    addLastChild();
                    continue;
                }
            } else {
                addCurrentChild();
            }
        }
    }

    /*
     * Decompose the path until it finds a node which branches are not all 
     * traversed.
     * */
    private boolean phase3() {
        while (!currentPath.isEmpty()) {
            if (currentPath.isBranch()) {
                if (this.countLoops() == 1) {
                    if (this.hasMoreChildren()) {
                        this.incChild();
                        return true;
                    } else {
                        this.removeFromTree();
                        currentPath.removeLast();
                    }
                } else {
                    this.removeFromTree();
                    currentPath.removeLast();
                }
            } else {
                currentPath.removeLast();
            }
        }
        return false;
    }

    private boolean hasMoreChildren() {
        PathElement e = (PathElement) stack.getLastLeaf().getUserObject();
        return e.currentChild + 1 < e.node.getChildren().size();
    }

    private void addLastChild() {
        PathElement e = (PathElement) stack.getLastLeaf().getUserObject();
        for (int i=e.node.getChildren().size()-1; i >= 0; i--) {
            if (i != e.currentChild) {
                currentPath.addLast(e.node.getChildren().get(i));
                break;
            }
        }
    }


    private void addCurrentChild() {
        if (currentPath.isBranch()) { // TODO WHY????
            PathElement last = (PathElement) stack.getLastLeaf().getUserObject();
            DataFlowNode inode = currentPath.getLast();
            if (inode.getChildren().size() > last.currentChild) { 
                // for some unknown reasons last.currentChild might not be a children of inode, see bug 1597987
                // see https://sourceforge.net/p/pmd/bugs/606/
                DataFlowNode child = inode.getChildren().get(last.currentChild);
                this.currentPath.addLast(child);
            }
        } else {
            DataFlowNode inode = currentPath.getLast();
            DataFlowNode child = inode.getChildren().get(0); //TODO ???? IMPORTANT - ERROR?
            this.currentPath.addLast(child);
        }
    }

//  ----------------------------------------------------------------------------
//	TREE FUNCTIONS
    
    /*
     * Adds a PathElement to a Tree, which contains information about
     * loops and "local scopes - encapsulation".
     * */
    private void addNodeToTree() {
        if (currentPath.isFirstDoStatement()) {
            DefaultMutableTreeNode level = stack;
            DataFlowNode doBranch = currentPath.getDoBranchNodeFromFirstDoStatement();

            while (true) {
                if (level.getChildCount() != 0) {
                    PathElement ref = this.isNodeInLevel(level);
                    if (ref != null) {
                        this.addRefPseudoPathElement(level, ref);
                        break;
                    } else {
                        level = this.getLastChildNode(level);
                        continue;
                    }
                } else {
                    this.addNewPseudoPathElement(level, doBranch);
                    break;
                }
            }
        }

        if (currentPath.isBranch()) {
            DefaultMutableTreeNode level = stack;

            if (currentPath.isDoBranchNode()) {
                while (!this.equalsPseudoPathElementWithDoBranchNodeInLevel(level)) {
                    level = this.getLastChildNode(level);
                    if (level.getChildCount() == 0) {
                        break;
                    }
                }
                PathElement ref = this.getDoBranchNodeInLevel(level);
                if (ref != null) {
                    addNode(level, ref);
                } else {
                    this.addNewPathElement(level);
                }

            } else {
                while (true) {
                    if (level.getChildCount() != 0) {
                        PathElement ref = this.isNodeInLevel(level);
                        if (ref != null) {
                            addNode(level, ref);
                            break;
                        } else {
                            level = this.getLastChildNode(level);
                            continue;
                        }
                    } else {
                        this.addNewPathElement(level);
                        break;
                    }
                }
            }
        }
    }

    private void removeFromTree() {
        DefaultMutableTreeNode last = stack.getLastLeaf();
        if (last == null) {
            System.out.println("removeFromTree - last == null");
            return;
        }
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) last.getParent();
        if (parent != null) {
        	// for some unknown reasons parent might be null, see bug 1597987
            parent.remove(last);
        }
        last = stack.getLastLeaf();
        if (last == null || last.getUserObject() == null) {
            return;
        }

        PathElement e = (PathElement) last.getUserObject();
        if (e != null && e.isPseudoPathElement()) {
            this.removeFromTree();
        }
    }

    private void addNewPathElement(DefaultMutableTreeNode level) {
        addNode(level, new PathElement(currentPath.getLast()));
    }

    /*
     * Needed for do loops
     * */
    private void addNewPseudoPathElement(DefaultMutableTreeNode level, DataFlowNode ref) {
        addNode(level, new PathElement(currentPath.getLast(), ref));
    }

    /*
     * Needed for do loops
     * */
    private void addRefPseudoPathElement(DefaultMutableTreeNode level, PathElement ref) {
        addNode(level, ref);
    }

    private boolean equalsPseudoPathElementWithDoBranchNodeInLevel(DefaultMutableTreeNode level) {
	DataFlowNode inode = currentPath.getLast();

        if (!inode.isType(NodeType.DO_EXPR)) {
            return false;
        }

        int childCount = level.getChildCount();
        DefaultMutableTreeNode child;

        for (int i = 0; i < childCount; i++) {
            child = (DefaultMutableTreeNode) level.getChildAt(i);
            PathElement pe = (PathElement) child.getUserObject();
            if (pe != null && pe.isPseudoPathElement() && pe.pseudoRef.equals(inode)) {
                return true;
            }
        }
        return false;
    }

    private PathElement getDoBranchNodeInLevel(DefaultMutableTreeNode level) {
	DataFlowNode inode = currentPath.getLast();
        if (!inode.isType(NodeType.DO_EXPR)) {
            return null;
        }

        int childCount = level.getChildCount();
        DefaultMutableTreeNode child;

        for (int i = 0; i < childCount; i++) {
            child = (DefaultMutableTreeNode) level.getChildAt(i);
            PathElement pe = (PathElement) child.getUserObject();
            if (inode.equals(pe.node)) {
                return pe;
            }
        }
        return null;
    }

    private void addNode(DefaultMutableTreeNode level, PathElement element) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode();
        node.setUserObject(element);
        level.add(node);
    }

    private PathElement isNodeInLevel(DefaultMutableTreeNode level) {
	DataFlowNode inode = currentPath.getLast();
        DefaultMutableTreeNode child = (DefaultMutableTreeNode) level.getFirstChild();

        if (child != null) {
            PathElement levelElement = (PathElement) child.getUserObject();
            if (inode.equals(levelElement.node)) {
                return levelElement;
            }
        }
        return null;
    }

    private DefaultMutableTreeNode getLastChildNode(DefaultMutableTreeNode node) {
        if (node.getChildCount() != 0) {
            return (DefaultMutableTreeNode) node.getLastChild();
        }
        return node;
    }

    private int countLoops() {
        DefaultMutableTreeNode treeNode = stack.getLastLeaf();
        int counter = 0;
        if (treeNode.getParent() != null) {
            // for some unknown reasons the parent of treeNode might be null, see bug 1597987
            // see https://sourceforge.net/p/pmd/bugs/606/
            int childCount = treeNode.getParent().getChildCount();
            for (int i = 0; i < childCount; i++) {
                DefaultMutableTreeNode tNode = (DefaultMutableTreeNode) treeNode.getParent().getChildAt(i);
                PathElement e = (PathElement) tNode.getUserObject();
                if (e != null && !e.isPseudoPathElement()) {
                    counter++;
                }
            }
        }
        return counter;
    }

    private void incChild() {
        ((PathElement) stack.getLastLeaf().getUserObject()).currentChild++;
    }

}
