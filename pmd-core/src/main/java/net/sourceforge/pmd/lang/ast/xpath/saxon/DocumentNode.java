/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ast.xpath.saxon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.saxon.om.Axis;
import net.sf.saxon.om.AxisIterator;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.Navigator;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SingleNodeIterator;
import net.sf.saxon.type.Type;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * A Saxon OM Document node for an AST Node.
 */
public class DocumentNode extends AbstractNodeInfo implements DocumentInfo {

    /**
     * The root ElementNode of the DocumentNode.
     */
    protected final ElementNode rootNode;

    /**
     * Mapping from AST Node to corresponding ElementNode.
     */
    public final Map<Node, ElementNode> nodeToElementNode = new HashMap<>();

    /**
     * Construct a DocumentNode, with the given AST Node serving as the root
     * ElementNode.
     * 
     * @param node The root AST Node.
     * 
     * @see ElementNode
     */
    public DocumentNode(Node node) {
	this.rootNode = new ElementNode(this, new IdGenerator(), null, node, -1);
    }

    /**
     * {@inheritDoc}
     */
    public String[] getUnparsedEntity(String name) {
	throw createUnsupportedOperationException("DocumentInfo.getUnparsedEntity(String)");
    }

    /**
     * {@inheritDoc}
     */
    public Iterator getUnparsedEntityNames() {
	throw createUnsupportedOperationException("DocumentInfo.getUnparsedEntityNames()");
    }

    /**
     * {@inheritDoc}
     */
    public NodeInfo selectID(String id) {
	throw createUnsupportedOperationException("DocumentInfo.selectID(String)");
    }

    @Override
    public int getNodeKind() {
	return Type.DOCUMENT;
    }

    @Override
    public DocumentInfo getDocumentRoot() {
	return this;
    }

    @Override
    public boolean hasChildNodes() {
	return true;
    }

    @Override
    public AxisIterator iterateAxis(byte axisNumber) {
	switch (axisNumber) {
	case Axis.DESCENDANT:
	    return new Navigator.DescendantEnumeration(this, false, true);
	case Axis.DESCENDANT_OR_SELF:
	    return new Navigator.DescendantEnumeration(this, true, true);
	case Axis.CHILD:
	    return SingleNodeIterator.makeIterator(rootNode);
	default:
	    return super.iterateAxis(axisNumber);
	}
    }
}
