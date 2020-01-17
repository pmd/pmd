/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath.saxon;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;

import net.sf.saxon.om.Axis;
import net.sf.saxon.om.AxisIterator;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.EmptyIterator;
import net.sf.saxon.om.Navigator;
import net.sf.saxon.om.NodeArrayIterator;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SingleNodeIterator;
import net.sf.saxon.type.Type;

/**
 * A Saxon OM Element type node for an AST Node.
 */
@Deprecated
@InternalApi
public class ElementNode extends AbstractNodeInfo {

    protected final DocumentNode document;
    protected final ElementNode parent;
    protected final Node node;
    protected final int id;
    protected final int siblingPosition;
    protected final NodeInfo[] children;

    public ElementNode(DocumentNode document, IdGenerator idGenerator, ElementNode parent, Node node,
            int siblingPosition) {
        this.document = document;
        this.parent = parent;
        this.node = node;
        this.id = idGenerator.getNextId();
        this.siblingPosition = siblingPosition;
        if (node.getNumChildren() > 0) {
            this.children = new NodeInfo[node.getNumChildren()];
            for (int i = 0; i < children.length; i++) {
                children[i] = new ElementNode(document, idGenerator, this, node.getChild(i), i);
            }
        } else {
            this.children = null;
        }
        document.nodeToElementNode.put(node, this);
    }

    @Override
    public Object getUnderlyingNode() {
        return node;
    }

    @Override
    public int getSiblingPosition() {
        return siblingPosition;
    }

    @Override
    public int getColumnNumber() {
        return node.getBeginColumn();
    }

    @Override
    public int getLineNumber() {
        return node.getBeginLine();
    }

    @Override
    public boolean hasChildNodes() {
        return children != null;
    }

    @Override
    public int getNodeKind() {
        return Type.ELEMENT;
    }

    @Override
    public DocumentInfo getDocumentRoot() {
        return document;
    }

    @Override
    public String getLocalPart() {
        return node.getXPathNodeName();
    }

    @Override
    public String getURI() {
        return "";
    }

    @Override
    public NodeInfo getParent() {
        return parent;
    }

    @Override
    public int compareOrder(NodeInfo other) {
        int result;
        if (this.isSameNodeInfo(other)) {
            result = 0;
        } else {
            result = Integer.signum(this.getLineNumber() - other.getLineNumber());
            if (result == 0) {
                result = Integer.signum(this.getColumnNumber() - other.getColumnNumber());
            }
            if (result == 0) {
                if (this.getParent().equals(other.getParent())) {
                    result = Integer.signum(this.getSiblingPosition() - ((ElementNode) other).getSiblingPosition());
                } else {
                    // we must not return 0 here, otherwise the node might be removed as duplicate when creating
                    // a union set. The the nodes are definitively different nodes (isSameNodeInfo == false).
                    result = 1;
                }
            }
        }
        return result;
    }

    @SuppressWarnings("PMD.MissingBreakInSwitch")
    @Override
    public AxisIterator iterateAxis(byte axisNumber) {
        switch (axisNumber) {
        case Axis.ANCESTOR:
            return new Navigator.AncestorEnumeration(this, false);
        case Axis.ANCESTOR_OR_SELF:
            return new Navigator.AncestorEnumeration(this, true);
        case Axis.ATTRIBUTE:
            return new AttributeAxisIterator(this);
        case Axis.CHILD:
            if (children == null) {
                return EmptyIterator.getInstance();
            } else {
                return new NodeArrayIterator(children);
            }
        case Axis.DESCENDANT:
            return new Navigator.DescendantEnumeration(this, false, true);
        case Axis.DESCENDANT_OR_SELF:
            return new Navigator.DescendantEnumeration(this, true, true);
        case Axis.FOLLOWING:
            return new Navigator.FollowingEnumeration(this);
        case Axis.FOLLOWING_SIBLING:
            if (parent == null || siblingPosition == parent.children.length - 1) {
                return EmptyIterator.getInstance();
            } else {
                return new NodeArrayIterator(parent.children, siblingPosition + 1, parent.children.length);
            }
        case Axis.NAMESPACE:
            return super.iterateAxis(axisNumber);
        case Axis.PARENT:
            return SingleNodeIterator.makeIterator(parent);
        case Axis.PRECEDING:
            return new Navigator.PrecedingEnumeration(this, false);
        case Axis.PRECEDING_SIBLING:
            if (parent == null || siblingPosition == 0) {
                return EmptyIterator.getInstance();
            } else {
                return new NodeArrayIterator(parent.children, 0, siblingPosition);
            }
        case Axis.SELF:
            return SingleNodeIterator.makeIterator(this);
        case Axis.PRECEDING_OR_ANCESTOR:
            return new Navigator.PrecedingEnumeration(this, true);
        default:
            return super.iterateAxis(axisNumber);
        }
    }

}
