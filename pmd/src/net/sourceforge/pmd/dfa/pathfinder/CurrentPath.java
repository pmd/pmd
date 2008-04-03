package net.sourceforge.pmd.dfa.pathfinder;

import java.util.Iterator;
import java.util.LinkedList;

import net.sourceforge.pmd.dfa.DataFlowNode;
import net.sourceforge.pmd.dfa.NodeType;

public class CurrentPath {

    private LinkedList<DataFlowNode> list;

    public CurrentPath() {
        list = new LinkedList<DataFlowNode>();
    }

    public int getLength() {
        return list.size();
    }
    
    public Iterator<DataFlowNode> iterator() {
        return list.iterator();
    }

    public DataFlowNode getLast() {
        return list.getLast();
    }

    public void removeLast() {
        list.removeLast();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public void addLast(DataFlowNode n) {
        list.addLast(n);
        //System.out.println("adding: " + n);
    }

    public boolean isDoBranchNode() {
        return list.getLast().isType(NodeType.DO_EXPR);
    }

    public boolean isFirstDoStatement() {
        return isFirstDoStatement(list.getLast());
    }

    public DataFlowNode getDoBranchNodeFromFirstDoStatement() {
	DataFlowNode inode = list.getLast();
        if (!isFirstDoStatement()) return null;
        for (DataFlowNode parent: inode.getParents()) {
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

    private boolean isFirstDoStatement(DataFlowNode inode) {
        int index = inode.getIndex() - 1;
        if (index < 0) return false;
        return ((DataFlowNode) inode.getFlow().get(index)).isType(NodeType.DO_BEFORE_FIRST_STATEMENT);
    }
}

