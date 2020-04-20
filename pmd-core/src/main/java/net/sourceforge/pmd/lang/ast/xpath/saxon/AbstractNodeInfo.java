/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath.saxon;

import net.sourceforge.pmd.annotation.InternalApi;

import net.sf.saxon.Configuration;
import net.sf.saxon.event.Receiver;
import net.sf.saxon.om.Axis;
import net.sf.saxon.om.AxisIterator;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.FastStringBuffer;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.om.Navigator.AxisFilter;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.SiblingCountingNode;
import net.sf.saxon.om.VirtualNode;
import net.sf.saxon.pattern.NodeTest;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.Value;

/**
 * This is a basic implementation of the Saxon NodeInfo and related interfaces.
 * Most methods are trivial implementations which immediately throw
 * {@link UnsupportedOperationException}. A few of the methods actually have
 * useful implementations, such as {@link #iterateAxis(byte, NodeTest)} and
 * {@link #isSameNodeInfo(NodeInfo)}.
 */
@Deprecated
@InternalApi
public class AbstractNodeInfo implements VirtualNode, SiblingCountingNode {
    @Override
    public String getSystemId() {
        throw createUnsupportedOperationException("Source.getSystemId()");
    }

    @Override
    public void setSystemId(String systemId) {
        throw createUnsupportedOperationException("Source.setSystemId(String)");
    }

    @Override
    public String getStringValue() {
        throw createUnsupportedOperationException("ValueRepresentation.getStringValue()");
    }

    @Override
    public CharSequence getStringValueCS() {
        throw createUnsupportedOperationException("ValueRepresentation.getStringValueCS()");
    }

    @Override
    public SequenceIterator getTypedValue() throws XPathException {
        throw createUnsupportedOperationException("Item.getTypedValue()");
    }

    @Override
    public Object getUnderlyingNode() {
        throw createUnsupportedOperationException("VirtualNode.getUnderlyingNode()");
    }

    @Override
    public int getSiblingPosition() {
        throw createUnsupportedOperationException("SiblingCountingNode.getSiblingPosition()");
    }

    @Override
    public Value atomize() throws XPathException {
        throw createUnsupportedOperationException("NodeInfo.atomize()");
    }

    @Override
    public int compareOrder(NodeInfo other) {
        throw createUnsupportedOperationException("NodeInfo.compareOrder(NodeInfo)");
    }

    @Override
    public void copy(Receiver receiver, int whichNamespaces, boolean copyAnnotations, int locationId)
            throws XPathException {
        throw createUnsupportedOperationException("ValueRepresentation.copy(Receiver, int, boolean, int)");
    }

    /**
     * This implementation considers to NodeInfo objects to be equal, if their
     * underlying nodes are equal.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ElementNode) {
            return this.getUnderlyingNode() == ((ElementNode) other).getUnderlyingNode();
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (this.getUnderlyingNode() != null) {
            return super.hashCode() + 31 * this.getUnderlyingNode().hashCode();
        } else {
            return super.hashCode();
        }
    }

    @Override
    public void generateId(FastStringBuffer buffer) {
        throw createUnsupportedOperationException("NodeInfo.generateId(FastStringBuffer)");
    }

    @Override
    public String getAttributeValue(int fingerprint) {
        throw createUnsupportedOperationException("NodeInfo.getAttributeValue(int)");
    }

    @Override
    public String getBaseURI() {
        throw createUnsupportedOperationException("NodeInfo.getBaseURI()");
    }

    @Override
    public int getColumnNumber() {
        throw createUnsupportedOperationException("NodeInfo.getColumnNumber()");
    }

    @Override
    public Configuration getConfiguration() {
        throw createUnsupportedOperationException("NodeInfo.getConfiguration()");
    }

    @Override
    public int[] getDeclaredNamespaces(int[] buffer) {
        throw createUnsupportedOperationException("NodeInfo.getDeclaredNamespaces(int[])");
    }

    @Override
    public String getDisplayName() {
        throw createUnsupportedOperationException("NodeInfo.getDisplayName()");
    }

    /**
     * This implementation always returns 0.
     *
     * {@inheritDoc}
     */
    @Override
    public int getDocumentNumber() {
        return 0;
    }

    @Override
    public DocumentInfo getDocumentRoot() {
        throw createUnsupportedOperationException("NodeInfo.getDocumentRoot()");
    }

    @Override
    public int getFingerprint() {
        throw createUnsupportedOperationException("NodeInfo.getFingerprint()");
    }

    @Override
    public int getLineNumber() {
        throw createUnsupportedOperationException("NodeInfo.getLineNumber()");
    }

    @Override
    public String getLocalPart() {
        throw createUnsupportedOperationException("NodeInfo.getLocalPart()");
    }

    @Override
    public int getNameCode() {
        throw createUnsupportedOperationException("NodeInfo.getNameCode()");
    }

    @Override
    public NamePool getNamePool() {
        throw createUnsupportedOperationException("NodeInfo.getNamePool()");
    }

    @Override
    public int getNodeKind() {
        throw createUnsupportedOperationException("NodeInfo.getNodeKind()");
    }

    @Override
    public NodeInfo getParent() {
        throw createUnsupportedOperationException("NodeInfo.getParent()");
    }

    @Override
    public String getPrefix() {
        throw createUnsupportedOperationException("NodeInfo.getPrefix()");
    }

    @Override
    public NodeInfo getRoot() {
        throw createUnsupportedOperationException("NodeInfo.getRoot()");
    }

    @Override
    public int getTypeAnnotation() {
        throw createUnsupportedOperationException("NodeInfo.getTypeAnnotation()");
    }

    @Override
    public String getURI() {
        throw createUnsupportedOperationException("NodeInfo.getURI()");
    }

    @Override
    public boolean hasChildNodes() {
        throw createUnsupportedOperationException("NodeInfo.hasChildNodes()");
    }

    @Override
    public boolean isId() {
        throw createUnsupportedOperationException("NodeInfo.isId()");
    }

    @Override
    public boolean isIdref() {
        throw createUnsupportedOperationException("NodeInfo.isIdref()");
    }

    @Override
    public boolean isNilled() {
        throw createUnsupportedOperationException("NodeInfo.isNilled()");
    }

    /**
     * This implementation delegates to {@link #equals(Object)}, per the Saxon
     * documentation's description of this method's behavior.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean isSameNodeInfo(NodeInfo other) {
        return this.equals(other);
    }

    @Override
    public AxisIterator iterateAxis(byte axisNumber) {
        throw createUnsupportedOperationException(
                "NodeInfo.iterateAxis(byte) for axis '" + Axis.axisName[axisNumber] + "'");
    }

    /**
     * This implementation calls {@link #iterateAxis(byte)} to get an
     * {@link AxisIterator} which is then optionally filtered using
     * {@link AxisFilter}.
     *
     * {@inheritDoc}
     */
    @Override
    public AxisIterator iterateAxis(byte axisNumber, NodeTest nodeTest) {
        return filter(iterateAxis(axisNumber), nodeTest);
    }

    protected static AxisIterator filter(AxisIterator axisIterator, NodeTest nodeTest) {
        return nodeTest != null ? new AxisFilter(axisIterator, nodeTest) : axisIterator;
    }

    /**
     * Used to create a customized instance of UnsupportedOperationException.
     * The caller of this method is intended to <code>throw</code> the
     * exception.
     *
     * @param name
     *            Method name that is not supported.
     * @return A UnsupportedOperationException indicated the method is not
     *         supported by the implementation class.
     */
    protected UnsupportedOperationException createUnsupportedOperationException(String name) {
        return new UnsupportedOperationException(name + " is not implemented by " + this.getClass().getName());
    }
}
