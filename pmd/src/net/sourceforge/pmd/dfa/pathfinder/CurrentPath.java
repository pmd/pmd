package net.sourceforge.pmd.dfa.pathfinder;

import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.dfa.NodeType;

import java.util.Iterator;
import java.util.LinkedList;

public class CurrentPath {

    private LinkedList list;

    public CurrentPath() {
        list = new LinkedList();
    }

    public int getLength() {
        return list.size();
    }
    
    public Iterator iterator() {
        return list.iterator();
    }

    public IDataFlowNode getLast() {
        return (IDataFlowNode) list.getLast();
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
        return ((IDataFlowNode) list.getLast()).isType(NodeType.DO_EXPR);
    }

    public boolean isFirstDoStatement() {
        return isFirstDoStatement((IDataFlowNode) list.getLast());
    }

    public IDataFlowNode getDoBranchNodeFromFirstDoStatement() {
        IDataFlowNode inode = (IDataFlowNode) list.getLast();
        if (!isFirstDoStatement()) return null;
        for (int i = 0; i < inode.getParents().size(); i++) {
            IDataFlowNode parent = (IDataFlowNode) inode.getParents().get(i);
            if (parent.isType(NodeType.DO_EXPR)) {
                return parent;
            }
        }
        return null;
    }

    public boolean isEndNode() {
        return ((IDataFlowNode) list.getLast()).getChildren().size() == 0;
        //return inode instanceof StartOrEndDataFlowNode;
    }

    public boolean isBranch() {
        return ((IDataFlowNode) list.getLast()).getChildren().size() > 1;
    }

    private boolean isFirstDoStatement(IDataFlowNode inode) {
        int index = inode.getIndex() - 1;
        if (index < 0) return false;
        return ((IDataFlowNode) inode.getFlow().get(index)).isType(NodeType.DO_BEFORE_FIRST_STATEMENT);
    }
}

