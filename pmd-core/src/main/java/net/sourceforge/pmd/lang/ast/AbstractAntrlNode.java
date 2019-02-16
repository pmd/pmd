package net.sourceforge.pmd.lang.ast;

import java.util.Iterator;
import java.util.List;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import org.jaxen.JaxenException;

public class AbstractAntrlNode implements AntlrNode {

    protected Node parent;

    private DataFlowNode dataFlowNode;
    private Object userData;

    @Override
    public int getBeginLine() {
        return 0;
    }

    @Override
    public int getBeginColumn() {
        return 0;
    }

    @Override
    public int getEndLine() {
        return 0;
    }

    @Override
    public int getEndColumn() {
        return 0;
    }

    @Override
    public DataFlowNode getDataFlowNode() {
        if (this.dataFlowNode == null) {
            if (this.parent != null) {
                return parent.getDataFlowNode();
            }
            return null; // TODO wise?
        }
        return dataFlowNode;
    }

    @Override
    public void setDataFlowNode(final DataFlowNode dataFlowNode) {
        this.dataFlowNode = dataFlowNode;
    }

    @Override
    public Node getNthParent(final int n) {
        return null;
    }

    @Override
    public <T> T getFirstParentOfType(final Class<T> parentType) {
        return null;
    }

    @Override
    public <T> List<T> getParentsOfType(final Class<T> parentType) {
        return null;
    }

    @Override
    public <T> T getFirstParentOfAnyType(final Class<? extends T>[] parentTypes) {
        return null;
    }

    @Override
    public <T> List<T> findChildrenOfType(final Class<T> childType) {
        return null;
    }

    @Override
    public <T> List<T> findDescendantsOfType(final Class<T> targetType) {
        return null;
    }

    @Override
    public <T> void findDescendantsOfType(final Class<T> targetType, final List<T> results,
        final boolean crossFindBoundaries) {

    }

    @Override
    public <T> T getFirstChildOfType(final Class<T> childType) {
        return null;
    }

    @Override
    public <T> T getFirstDescendantOfType(final Class<T> descendantType) {
        return null;
    }

    @Override
    public <T> boolean hasDescendantOfType(final Class<T> type) {
        return false;
    }

    @Override
    public List<? extends Node> findChildNodesWithXPath(final String xpathString) throws JaxenException {
        return null;
    }

    @Override
    public boolean hasDescendantMatchingXPath(final String xpathString) {
        return false;
    }

    @Override
    public Object getUserData() {
        return userData;
    }

    @Override
    public void setUserData(final Object userData) {
        this.userData = userData;
    }

    // TODO: should we make it abstract due to the comment in AbstractNode ?
    @Override
    public String getXPathNodeName() {
        return toString();
    }

    @Override
    public Iterator<Attribute> getXPathAttributesIterator() {
        return null;
    }
}
