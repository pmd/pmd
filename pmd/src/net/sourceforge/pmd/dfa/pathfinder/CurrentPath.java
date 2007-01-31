package net.sourceforge.pmd.dfa.pathfinder;

import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.dfa.NodeType;

import java.util.Iterator;
import java.util.LinkedList;

public class CurrentPath {

    private LinkedList<IDataFlowNode> list;

    public CurrentPath() {
        list = new LinkedList<IDataFlowNode>();
    }

    public int getLength() {
        return list.size();
    }
    
    public Iterator<IDataFlowNode> iterator() {
        return list.iterator();
    }

    public IDataFlowNode getLast() {
        return list.getLast();
    }

    public void removeLast() {
        list.removeLast();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public void addLast(IDataFlowNode n) {
        list.addLast(n);
        //System.out.println("adding: " + n);
    }

    public boolean isDoBranchNode() {
        return list.getLast().isType(NodeType.DO_EXPR);
    }

    public boolean isFirstDoStatement() {
        return isFirstDoStatement(list.getLast());
    }

    public IDataFlowNode getDoBranchNodeFromFirstDoStatement() {
        IDataFlowNode inode = list.getLast();
        if (!isFirstDoStatement()) return null;
        for (IDataFlowNode parent: inode.getParents()) {
            if (parent.isType(NodeType.DO_EXPR)) {
                return parent;
            }
        }
        return null;
    }

    public boolean isEndNode() {
        return list.getLast().getChildren().size() == 0;
        //return inode instanceof StartOrEndDataFlowNode;
    }

    public boolean isBranch() {
        return list.getLast().getChildren().size() > 1;
    }

    private boolean isFirstDoStatement(IDataFlowNode inode) {
        int index = inode.getIndex() - 1;
        if (index < 0) return false;
        return ((IDataFlowNode) inode.getFlow().get(index)).isType(NodeType.DO_BEFORE_FIRST_STATEMENT);
    }
}

