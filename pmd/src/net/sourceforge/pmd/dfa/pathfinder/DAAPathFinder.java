/*
 * Created on 09.08.2004
 */
package net.sourceforge.pmd.dfa.pathfinder;

import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.dfa.NodeType;
import net.sourceforge.pmd.dfa.StartOrEndDataFlowNode;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.LinkedList;

/**
 * @author raik
 *         <p/>
 *         Finds all paths of a data flow. Each loop will be 0 or 2 times traversed ->
 *         2 paths. This is special to the data flow anomaly analysis.
 */
public class DAAPathFinder {

    private IDataFlowNode rootNode;
    private Executable shim;

    private CurrentPath currentPath = new CurrentPath();

    private DefaultMutableTreeNode stack = new DefaultMutableTreeNode();
    private static final int MAX_PATHS = 5000;

    private static class PathElement{
        int currentChild;
        IDataFlowNode node;
        IDataFlowNode pseudoRef;
        PathElement(IDataFlowNode node) {
            this.node = node;
        }
    }

    public DAAPathFinder(IDataFlowNode rootNode, Executable shim) {
        this.rootNode = rootNode;
        this.shim = shim;
    }

    public void run() {
        phase1(rootNode);
    }

    /*
     * Initialise the path search. Starts the searching.
     * */
    private void phase1(IDataFlowNode startNode) {
        this.currentPath.clear();
        this.currentPath.addLast(startNode);

        int i = 0;
        boolean flag = true;
        do {
            i++;
            phase2(flag);
            shim.execute(currentPath);
            flag = false;
        } while (i < MAX_PATHS && phase3());
        //System.out.println("found: " + i + " path(s)");
    }

    /*
     * Builds up the path.
     * */
    private void phase2(boolean flag) {
        while (!currentPath.isEndNode()) {
            if (currentPath.isBranch() || currentPath.isFirstDoStatement()) {
                if (flag) {
                    addNodeToTree();
                }
                flag = true;
                if (countLoops() <= 2) {
                    addCurrentChild();
                    continue;
                } else {
                    addSecondChild();
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
        PathElement e = (PathElement) getLastNode().getUserObject();
        return e.currentChild + 1 < e.node.getChildren().size();
    }

    private void addSecondChild() {
        PathElement e = (PathElement) this.getLastNode().getUserObject();
        int secondChild;
        if (e.currentChild == 1) {
            secondChild = 0;
        } else {
            secondChild = 1;
        }
        IDataFlowNode child = (IDataFlowNode) e.node.getChildren().get(secondChild);
        this.currentPath.addLast(child);
    }

    private void addCurrentChild() {
        if (currentPath.isBranch()) { // TODO WHY????
            PathElement last = (PathElement) this.getLastNode().getUserObject();
            IDataFlowNode inode = currentPath.getLast();
            IDataFlowNode child = (IDataFlowNode) inode.getChildren().get(last.currentChild);
            this.currentPath.addLast(child);
        } else {
            IDataFlowNode inode = currentPath.getLast();
            IDataFlowNode child = (IDataFlowNode) inode.getChildren().get(0); //TODO ???? IMPORTANT - ERROR?
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
            DefaultMutableTreeNode level = this.getRootNode();
            IDataFlowNode doBranch = currentPath.getDoBranchNodeFromFirstDoStatement();

            while (true) {
                if (this.hasTheLevelChildren(level)) {
                    PathElement ref;
                    if ((ref = this.isNodeInLevel(level)) != null) {
                        //addRefPseudoNode
                        this.addRefPseudoPathElement(level, ref);
                        break;
                    } else {
                        level = this.getLastChildNode(level);
                        continue;
                    }
                } else {
                    //addNewPseudoNode
                    this.addNewPseudoPathElement(level, doBranch);
                    break;
                }
            }
        }

        if (currentPath.isBranch()) {
            DefaultMutableTreeNode level = this.getRootNode();

            if (currentPath.isDoBranchNode()) {
                while (!this.equalsPseudoPathElementWithDoBranchNodeInLevel(level)) {
                    level = this.getLastChildNode(level);
                }
                PathElement ref;
                if ((ref = this.getDoBranchNodeInLevel(level)) != null) {
                    //addRefNode
                    this.addRefPathElement(level, ref);
                } else {
                    //createNewNode
                    this.addNewPathElement(level);
                }

            } else {
                while (true) {
                    if (this.hasTheLevelChildren(level)) {
                        PathElement ref;
                        if ((ref = this.isNodeInLevel(level)) != null) {
                            //addRefNode
                            this.addRefPathElement(level, ref);
                            break;
                        } else {
                            level = this.getLastChildNode(level);
                            continue;
                        }
                    } else {
                        //createNewNode
                        this.addNewPathElement(level);
                        break;
                    }
                }
            }
        }
    }

    private void removeFromTree() {
        DefaultMutableTreeNode last = this.getLastNode();
        if (last == null) {
            System.out.println("removeFromTree - last == null");
            return;
        }
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) last.getParent();
        parent.remove(last);
        last = this.getLastNode();
        if (last == null || last.getUserObject() == null) return;

        PathElement e = (PathElement) last.getUserObject();
        if (this.isPseudoPathElement(e)) {
            this.removeFromTree();
        }
    }

    private void addNewPathElement(DefaultMutableTreeNode level) {
        IDataFlowNode last = (IDataFlowNode) this.currentPath.getLast();
        PathElement e = new PathElement(last);
        this.addNode(level, e);
    }

    private void addRefPathElement(DefaultMutableTreeNode level, PathElement ref) {
        this.addNode(level, ref);
    }

    /*
     * Needed for do loops
     * */
    private void addNewPseudoPathElement(DefaultMutableTreeNode level, IDataFlowNode ref) {
        IDataFlowNode last = (IDataFlowNode) this.currentPath.getLast();
        PathElement e = new PathElement(last);
        e.pseudoRef = ref;
        this.addNode(level, e);
    }

    /*
     * Needed for do loops
     * */
    private void addRefPseudoPathElement(DefaultMutableTreeNode level, PathElement ref) {
        this.addNode(level, ref);
    }

    private boolean equalsPseudoPathElementWithDoBranchNodeInLevel(DefaultMutableTreeNode level) {
        IDataFlowNode inode = (IDataFlowNode) this.currentPath.getLast();

        if (!inode.isType(NodeType.DO_EXPR)) return false;

        int childCount = level.getChildCount();
        DefaultMutableTreeNode child;

        for (int i = 0; i < childCount; i++) {
            child = (DefaultMutableTreeNode) level.getChildAt(i);
            PathElement pe = (PathElement) child.getUserObject();
            if (isPseudoPathElement(pe) && pe.pseudoRef.equals(inode)) {
                return true;
            }
        }
        return false;
    }

    private PathElement getDoBranchNodeInLevel(DefaultMutableTreeNode level) {
        IDataFlowNode inode = (IDataFlowNode)currentPath.getLast();
        if (!inode.isType(NodeType.DO_EXPR)) return null;

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
        IDataFlowNode inode = (IDataFlowNode) this.currentPath.getLast();
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
        if (this.hasTheLevelChildren(node)) {
            return (DefaultMutableTreeNode) node.getLastChild();
        }
        return node;
    }

    private DefaultMutableTreeNode getRootNode() {
        return this.stack;
    }

    private boolean hasTheLevelChildren(DefaultMutableTreeNode level) {
        return level.getChildCount() != 0;
    }

    private DefaultMutableTreeNode getLastNode() {
        return this.stack.getLastLeaf();
    }

    private int countLoops() {
        DefaultMutableTreeNode treeNode = this.getLastNode();
        int counter = 0;
        int childCount = treeNode.getParent().getChildCount();
        for (int i = 0; i < childCount; i++) {
            DefaultMutableTreeNode tNode = (DefaultMutableTreeNode) treeNode.getParent().getChildAt(i);
            PathElement e = (PathElement) tNode.getUserObject();
            if (!this.isPseudoPathElement(e)) {
                counter++;
            }
        }
        return counter;
    }

    private void incChild() {
        DefaultMutableTreeNode last = this.getLastNode();
        PathElement e = (PathElement) last.getUserObject();
        e.currentChild++;
    }

    private boolean isPseudoPathElement(PathElement pe) {
        return pe != null && pe.pseudoRef != null;
    }
}
