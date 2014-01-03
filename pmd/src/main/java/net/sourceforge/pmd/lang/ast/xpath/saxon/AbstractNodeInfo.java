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
    public String getSystemId() {
	throw createUnsupportedOperationException("Source.getSystemId()");
    }

    /**
     * {@inheritDoc}
     */
    public void setSystemId(String systemId) {
	throw createUnsupportedOperationException("Source.setSystemId(String)");
    }

    /**
     * {@inheritDoc}
     */
    public String getStringValue() {
	throw createUnsupportedOperationException("ValueRepresentation.getStringValue()");
    }

    /**
     * {@inheritDoc}
     */
    public CharSequence getStringValueCS() {
	throw createUnsupportedOperationException("ValueRepresentation.getStringValueCS()");
    }

    /**
     * {@inheritDoc}
     */
    public SequenceIterator getTypedValue() throws XPathException {
	throw createUnsupportedOperationException("Item.getTypedValue()");
    }

    /**
     * {@inheritDoc}
     */
    public Object getUnderlyingNode() {
	throw createUnsupportedOperationException("VirtualNode.getUnderlyingNode()");
    }

    /**
     * {@inheritDoc}
     */
    public int getSiblingPosition() {
	throw createUnsupportedOperationException("SiblingCountingNode.getSiblingPosition()");
    }

    /**
     * {@inheritDoc}
     */
    public Value atomize() throws XPathException {
	throw createUnsupportedOperationException("NodeInfo.atomize()");
    }

    /**
     * {@inheritDoc}
     */
    public int compareOrder(NodeInfo other) {
	throw createUnsupportedOperationException("NodeInfo.compareOrder(NodeInfo)");
    }

    /**
     * {@inheritDoc}
     */
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
    public void generateId(FastStringBuffer buffer) {
	throw createUnsupportedOperationException("NodeInfo.generateId(FastStringBuffer)");
    }

    /**
     * {@inheritDoc}
     */
    public String getAttributeValue(int fingerprint) {
	throw createUnsupportedOperationException("NodeInfo.getAttributeValue(int)");
    }

    /**
     * {@inheritDoc}
     */
    public String getBaseURI() {
	throw createUnsupportedOperationException("NodeInfo.getBaseURI()");
    }

    /**
     * {@inheritDoc}
     */
    public int getColumnNumber() {
	throw createUnsupportedOperationException("NodeInfo.getColumnNumber()");
    }

    /**
     * {@inheritDoc}
     */
    public Configuration getConfiguration() {
	throw createUnsupportedOperationException("NodeInfo.getConfiguration()");
    }

    /**
     * {@inheritDoc}
     */
    public int[] getDeclaredNamespaces(int[] buffer) {
	throw createUnsupportedOperationException("NodeInfo.getDeclaredNamespaces(int[])");
    }

    /**
     * {@inheritDoc}
     */
    public String getDisplayName() {
	throw createUnsupportedOperationException("NodeInfo.getDisplayName()");
    }

    /**
     * This implementation always returns 0.
     *
     * {@inheritDoc}
     */
    public int getDocumentNumber() {
	return 0;
    }

    /**
     * {@inheritDoc}
     */
    public DocumentInfo getDocumentRoot() {
	throw createUnsupportedOperationException("NodeInfo.getDocumentRoot()");
    }

    /**
     * {@inheritDoc}
     */
    public int getFingerprint() {
	throw createUnsupportedOperationException("NodeInfo.getFingerprint()");
    }

    /**
     * {@inheritDoc}
     */
    public int getLineNumber() {
	throw createUnsupportedOperationException("NodeInfo.getLineNumber()");
    }

    /**
     * {@inheritDoc}
     */
    public String getLocalPart() {
	throw createUnsupportedOperationException("NodeInfo.getLocalPart()");
    }

    /**
     * {@inheritDoc}
     */
    public int getNameCode() {
	throw createUnsupportedOperationException("NodeInfo.getNameCode()");
    }

    /**
     * {@inheritDoc}
     */
    public NamePool getNamePool() {
	throw createUnsupportedOperationException("NodeInfo.getNamePool()");
    }

    /**
     * {@inheritDoc}
     */
    public int getNodeKind() {
	throw createUnsupportedOperationException("NodeInfo.getNodeKind()");
    }

    /**
     * {@inheritDoc}
     */
    public NodeInfo getParent() {
	throw createUnsupportedOperationException("NodeInfo.getParent()");
    }

    /**
     * {@inheritDoc}
     */
    public String getPrefix() {
	throw createUnsupportedOperationException("NodeInfo.getPrefix()");
    }

    /**
     * {@inheritDoc}
     */
    public NodeInfo getRoot() {
	throw createUnsupportedOperationException("NodeInfo.getRoot()");
    }

    /**
     * {@inheritDoc}
     */
    public int getTypeAnnotation() {
	throw createUnsupportedOperationException("NodeInfo.getTypeAnnotation()");
    }

    /**
     * {@inheritDoc}
     */
    public String getURI() {
	throw createUnsupportedOperationException("NodeInfo.getURI()");
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasChildNodes() {
	throw createUnsupportedOperationException("NodeInfo.hasChildNodes()");
    }

    /**
     * {@inheritDoc}
     */
    public boolean isId() {
	throw createUnsupportedOperationException("NodeInfo.isId()");
    }

    /**
     * {@inheritDoc}
     */
    public boolean isIdref() {
	throw createUnsupportedOperationException("NodeInfo.isIdref()");
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNilled() {
	throw createUnsupportedOperationException("NodeInfo.isNilled()");
    }

    /**
     * This implementation delegates to {@link #equals(Object)}, per the Saxon
     * documentation's description of this method's behavior.
     *
     * {@inheritDoc}
     */
    public boolean isSameNodeInfo(NodeInfo other) {
	return this.equals(other);
    }

    /**
     * {@inheritDoc}
     */
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
