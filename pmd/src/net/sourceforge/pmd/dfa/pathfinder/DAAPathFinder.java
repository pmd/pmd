/*
 * Created on 09.08.2004
 */
package net.sourceforge.pmd.dfa.pathfinder;

import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.dfa.NodeType;

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
    private LinkedList currentPath = new LinkedList();
    private DefaultMutableTreeNode stack = new DefaultMutableTreeNode();
    private static final int MAX_PATHS = 5000;

    private static class PathElement {
        int currentChild;
        IDataFlowNode node;
        IDataFlowNode pseudoRef;
        PathElement(IDataFlowNode node) {
            this.node = node;
        }
    }

    public DAAPathFinder(IDataFlowNode rootNode, Executable exe) {
        this.rootNode = rootNode;
        this.shim = exe;
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
        while (!isEndNode()) {
            if (isBranch() || isFirstDoStatement()) {
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

        while (!this.isEmpty()) {
            if (this.isBranch()) {
                if (this.countLoops() == 1) {
                    if (this.hasMoreChildren()) {
                        this.incChild();
                        return true;
                    } else {
                        this.removeFromTree();
                        this.removeFromList();
                    }
                } else {
                    this.removeFromTree();
                    this.removeFromList();
                }
            } else {
                this.removeFromList();
            }
        }

        return false;
    }

    private void removeFromList() {
        this.currentPath.removeLast();
    }

    private boolean isEmpty() {
        return this.currentPath.isEmpty();
    }

    private boolean hasMoreChildren() {
        DefaultMutableTreeNode last = this.getLastNode();
        PathElement e = (PathElement) last.getUserObject();
        return e.currentChild + 1 < e.node.getChildren().size();
    }

    private boolean isEndNode() {
        IDataFlowNode inode = (IDataFlowNode) this.currentPath.getLast();
        // TODO use instanceof StartOrEndNode?
        return inode.getChildren().size() == 0;
    }

    private boolean isBranch() {
        IDataFlowNode inode = (IDataFlowNode) this.currentPath.getLast();
        return inode.getChildren().size() > 1;
    }

    private boolean isFirstDoStatement() {
        IDataFlowNode inode = (IDataFlowNode)this.currentPath.getLast();
        return this.isFirstDoStatement(inode);
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
        if (this.isBranch()) { // TODO WHY????
            PathElement last = (PathElement) this.getLastNode().getUserObject();
            IDataFlowNode inode = (IDataFlowNode) this.currentPath.getLast();
            IDataFlowNode child = (IDataFlowNode) inode.getChildren().get(last.currentChild);
            this.currentPath.addLast(child);
        } else {
            IDataFlowNode inode = (IDataFlowNode) this.currentPath.getLast();
            IDataFlowNode child = (IDataFlowNode) inode.getChildren().get(0); //TODO ???? IMPORTANT - ERROR?
            this.currentPath.addLast(child);
        }
    }

    private boolean isFirstDoStatement(IDataFlowNode inode) {
        int index = inode.getIndex() - 1;
        if (index < 0) return false;

        IDataFlowNode before = (IDataFlowNode) inode.getFlow().get(index);
        return before.isType(NodeType.DO_BEFORE_FIRST_STATEMENT);
    }

    private boolean isDoBranchNode() {
        IDataFlowNode last = (IDataFlowNode) this.currentPath.getLast();
        return this.isDoBranch(last);
    }

    private boolean isDoBranch(IDataFlowNode inode) {
        return inode.isType(NodeType.DO_EXPR);
    }
    
//  ----------------------------------------------------------------------------
//	TREE FUNCTIONS
    
    /*
     * Adds a PathElement to a Tree, which contains information about
     * loops and "local scopes - encapsulation".
     * */
    private void addNodeToTree() {
        if (this.isFirstDoStatement()) {
            DefaultMutableTreeNode level = this.getRootNode();
            IDataFlowNode doBranch = this.getDoBranchNodeFromFirstDoStatement();

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

        if (this.isBranch()) {
            DefaultMutableTreeNode level = this.getRootNode();

            if (this.isDoBranchNode()) {
                while (!this.equalsPseudoPathElementWithDoBranchNodeInLevel(level)) {
                    level = this.getLastChildNode(level);
                }
                PathElement ref;
                if ((ref = this.getDoBranchNodeInLevel(level)) != null) {
                    //addRefNode
                    this.addRefPathElement(level, ref);
                } else {
                    //addNewNode
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
                        //addNewNode
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

        if (last == null) return;
        if (last.getUserObject() == null) return;

        PathElement e = (PathElement) last.getUserObject();
        if (this.isPseudoPathElement(e)) {
            this.removeFromTree();
        }
    }

    private IDataFlowNode getDoBranchNodeFromFirstDoStatement() {
        IDataFlowNode inode = (IDataFlowNode) this.currentPath.getLast();

        if (!this.isFirstDoStatement()) return null;

        for (int i = 0; i < inode.getParents().size(); i++) {
            IDataFlowNode parent = (IDataFlowNode) inode.getParents().get(i);

            if (this.isDoBranch(parent)) {
                return parent;
            }
        }
        return null;
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

        if (!this.isDoBranch(inode)) return false;

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
        IDataFlowNode inode = (IDataFlowNode) this.currentPath.getLast();

        if (!this.isDoBranch(inode)) return null;

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
