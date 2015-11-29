/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.dfa.report;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractReportNode {
    private List<AbstractReportNode> childNodes = new ArrayList<>();
    private AbstractReportNode parentNode = null;

    /*
    * Number of all RuleViolations down to this node. At the moment it will
    * only be calculated by running the ReportHTMLPrintVisitor.
    * */
    private int numberOfViolations;

    /**
     * Should compare to nodes of the tree.
     */
    public abstract boolean equalsNode(AbstractReportNode arg0);

    /**
     * @return null If there isn't any child.
     */
    public AbstractReportNode getFirstChild() {
        if (this.isLeaf()) {
            return null;
        }
        return this.childNodes.get(0);
    }

    /**
     * @return null If there isn't any sibling.
     */
    public AbstractReportNode getNextSibling() {
        if (parentNode == null) {
            return null;
        }
        int index = parentNode.getChildIndex(this);
        if (index < 0) {
            return null;
        }
        if (index >= parentNode.childNodes.size() - 1) {
            return null;
        }
        return parentNode.childNodes.get(index + 1);
    }

    /**
     * @return index The index of the x-th child of his parent.
     */
    private int getChildIndex(AbstractReportNode child) {
        for (int i = 0; i < childNodes.size(); i++) {
            if (childNodes.get(i).equals(child)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Adds the child in front of any other childs.
     */
    public void addFirst(AbstractReportNode child) {
        childNodes.add(0, child);
        child.parentNode = this;
    }

    /**
     * Adds the child at the end.
     */
    public void add(AbstractReportNode child) {
        childNodes.add(child);
        child.parentNode = this;
    }

    public void addNumberOfViolation(int number) {
        numberOfViolations += number;
    }

    /**
     * @return The number of all violations downside the node.
     */
    public int getNumberOfViolations() {
        return numberOfViolations;
    }

    // ----------------------------------------------------------------------------
    // visitor methods
    public void childrenAccept(ReportVisitor visitor) {
        for (int i = 0; i < childNodes.size(); i++) {
            AbstractReportNode node = childNodes.get(i);
            node.accept(visitor);
        }
    }

    public void accept(ReportVisitor visitor) {
        visitor.visit(this);
    }

    public AbstractReportNode getChildAt(int arg0) {
        if (arg0 >= 0 && arg0 <= childNodes.size() - 1) {
            return childNodes.get(arg0);
        }
        return null;
    }

    public int getChildCount() {
        return childNodes.size();
    }

    public AbstractReportNode getParent() {
        return parentNode;
    }

    public boolean isLeaf() {
        return childNodes.isEmpty();
    }

}
