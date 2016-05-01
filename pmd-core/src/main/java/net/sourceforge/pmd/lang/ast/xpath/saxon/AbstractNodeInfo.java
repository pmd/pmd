/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ast.xpath.saxon;

import net.sf.saxon.Configuration;
import net.sf.saxon.event.Receiver;
import net.sf.saxon.om.Axis;
import net.sf.saxon.om.AxisIterator;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.FastStringBuffer;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.SiblingCountingNode;
import net.sf.saxon.om.VirtualNode;
import net.sf.saxon.om.Navigator.AxisFilter;
import net.sf.saxon.pattern.NodeTest;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.Value;

/**
 * This is a basic implementation of the Saxon NodeInfo and related interfaces.
 * Most methods are trivial implementations which immediately throw
 * {@link UnsupportedOperationException}.  A few of the methods actually have
 * useful implementations, such as {@link #iterateAxis(byte, NodeTest)} and
 * {@link #isSameNodeInfo(NodeInfo)}.
 */
public class AbstractNodeInfo implements VirtualNode, SiblingCountingNode {
    /**
     * {@inheritDoc}
     */
    @Override
    public String getSystemId() {
	throw createUnsupportedOperationException("Source.getSystemId()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSystemId(String systemId) {
	throw createUnsupportedOperationException("Source.setSystemId(String)");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringValue() {
	throw createUnsupportedOperationException("ValueRepresentation.getStringValue()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharSequence getStringValueCS() {
	throw createUnsupportedOperationException("ValueRepresentation.getStringValueCS()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SequenceIterator getTypedValue() throws XPathException {
	throw createUnsupportedOperationException("Item.getTypedValue()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getUnderlyingNode() {
	throw createUnsupportedOperationException("VirtualNode.getUnderlyingNode()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSiblingPosition() {
	throw createUnsupportedOperationException("SiblingCountingNode.getSiblingPosition()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Value atomize() throws XPathException {
	throw createUnsupportedOperationException("NodeInfo.atomize()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareOrder(NodeInfo other) {
	throw createUnsupportedOperationException("NodeInfo.compareOrder(NodeInfo)");
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateId(FastStringBuffer buffer) {
	throw createUnsupportedOperationException("NodeInfo.generateId(FastStringBuffer)");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAttributeValue(int fingerprint) {
	throw createUnsupportedOperationException("NodeInfo.getAttributeValue(int)");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBaseURI() {
	throw createUnsupportedOperationException("NodeInfo.getBaseURI()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnNumber() {
	throw createUnsupportedOperationException("NodeInfo.getColumnNumber()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration getConfiguration() {
	throw createUnsupportedOperationException("NodeInfo.getConfiguration()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getDeclaredNamespaces(int[] buffer) {
	throw createUnsupportedOperationException("NodeInfo.getDeclaredNamespaces(int[])");
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public DocumentInfo getDocumentRoot() {
	throw createUnsupportedOperationException("NodeInfo.getDocumentRoot()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFingerprint() {
	throw createUnsupportedOperationException("NodeInfo.getFingerprint()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLineNumber() {
	throw createUnsupportedOperationException("NodeInfo.getLineNumber()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocalPart() {
	throw createUnsupportedOperationException("NodeInfo.getLocalPart()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNameCode() {
	throw createUnsupportedOperationException("NodeInfo.getNameCode()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NamePool getNamePool() {
	throw createUnsupportedOperationException("NodeInfo.getNamePool()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNodeKind() {
	throw createUnsupportedOperationException("NodeInfo.getNodeKind()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeInfo getParent() {
	throw createUnsupportedOperationException("NodeInfo.getParent()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPrefix() {
	throw createUnsupportedOperationException("NodeInfo.getPrefix()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeInfo getRoot() {
	throw createUnsupportedOperationException("NodeInfo.getRoot()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTypeAnnotation() {
	throw createUnsupportedOperationException("NodeInfo.getTypeAnnotation()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getURI() {
	throw createUnsupportedOperationException("NodeInfo.getURI()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasChildNodes() {
	throw createUnsupportedOperationException("NodeInfo.hasChildNodes()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isId() {
	throw createUnsupportedOperationException("NodeInfo.isId()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isIdref() {
	throw createUnsupportedOperationException("NodeInfo.isIdref()");
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public AxisIterator iterateAxis(byte axisNumber) {
	throw createUnsupportedOperationException("NodeInfo.iterateAxis(byte) for axis '" + Axis.axisName[axisNumber]
		+ "'");
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
	AxisIterator axisIterator = iterateAxis(axisNumber);
	if (nodeTest != null) {
	    axisIterator = new AxisFilter(axisIterator, nodeTest);
	}
	return axisIterator;
    }

    /**
     * Used to create a customized instance of UnsupportedOperationException.
     * The caller of this method is intended to <code>throw</code> the exception.
     *
     * @param name Method name that is not supported.
     * @return A UnsupportedOperationException indicated the method is not supported by the implementation class.
     */
    protected UnsupportedOperationException createUnsupportedOperationException(String name) {
	return new UnsupportedOperationException(name + " is not implemented by " + this.getClass().getName());
    }
}
