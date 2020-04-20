/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath.saxon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.SaxonXPathRuleQuery;

import net.sf.saxon.om.Axis;
import net.sf.saxon.om.AxisIterator;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.EmptyIterator;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.om.Navigator;
import net.sf.saxon.om.Navigator.BaseEnumeration;
import net.sf.saxon.om.NodeArrayIterator;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.SingleNodeIterator;
import net.sf.saxon.om.SingletonIterator;
import net.sf.saxon.pattern.NameTest;
import net.sf.saxon.pattern.NodeTest;
import net.sf.saxon.type.Type;
import net.sf.saxon.value.AtomicValue;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.value.UntypedAtomicValue;
import net.sf.saxon.value.Value;

/**
 * A Saxon OM Element type node for an AST Node.
 */
@Deprecated
@InternalApi
public class ElementNode extends BaseNodeInfo {

    protected final DocumentNode document;
    protected final ElementNode parent;
    protected final Node node;
    protected final int id;
    protected final int siblingPosition;
    protected final NodeInfo[] children;

    private Map<Integer, AttributeNode> attributes;

    @Deprecated
    public ElementNode(DocumentNode document, IdGenerator idGenerator, ElementNode parent, Node node, int siblingPosition) {
        this(document, idGenerator, parent, node, siblingPosition, SaxonXPathRuleQuery.getNamePool());
    }

    public ElementNode(DocumentNode document,
                       IdGenerator idGenerator,
                       ElementNode parent,
                       Node node,
                       int siblingPosition,
                       NamePool namePool) {
        super(Type.ELEMENT, namePool, node.getXPathNodeName(), parent);

        this.document = document;
        this.parent = parent;
        this.node = node;
        this.id = idGenerator.getNextId();
        this.siblingPosition = siblingPosition;

        if (node.getNumChildren() > 0) {
            this.children = new NodeInfo[node.getNumChildren()];
            for (int i = 0; i < children.length; i++) {
                children[i] = new ElementNode(document, idGenerator, this, node.getChild(i), i, namePool);
            }
        } else {
            this.children = null;
        }
        document.nodeToElementNode.put(node, this);
    }

    private Map<Integer, AttributeNode> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<>();
            Iterator<Attribute> iter = node.getXPathAttributesIterator();
            int idx = 0;
            while (iter.hasNext()) {
                Attribute next = iter.next();
                AttributeNode attrNode = new AttributeNode(this, next, idx++);
                attributes.put(attrNode.getFingerprint(), attrNode);
            }
        }
        return attributes;
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
    public DocumentInfo getDocumentRoot() {
        return document;
    }

    @Override
    public String getLocalPart() {
        return node.getXPathNodeName();
    }


    @Override
    public SequenceIterator getTypedValue() {
        return SingletonIterator.makeIterator((AtomicValue) atomize());
    }

    @Override
    public Value atomize() {
        switch (getNodeKind()) {
        case Type.COMMENT:
        case Type.PROCESSING_INSTRUCTION:
            return new StringValue(getStringValueCS());
        default:
            return new UntypedAtomicValue(getStringValueCS());
        }
    }

    @Override
    public CharSequence getStringValueCS() {
        return "";
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


    @Override
    public String getDisplayName() {
        return getLocalPart();
    }


    @Override
    public AxisIterator iterateAxis(byte axisNumber, NodeTest nodeTest) {
        if (axisNumber == Axis.ATTRIBUTE) {
            if (nodeTest instanceof NameTest) {
                if ((nodeTest.getNodeKindMask() & (1 << Type.ATTRIBUTE)) == 0) {
                    return EmptyIterator.getInstance();
                } else {
                    int fp = nodeTest.getFingerprint();
                    if (fp != -1) {
                        return SingleNodeIterator.makeIterator(getAttributes().get(fp));
                    }
                }
            }
        }
        return super.iterateAxis(axisNumber, nodeTest);
    }

    @SuppressWarnings("PMD.MissingBreakInSwitch")
    @Override
    public AxisIterator iterateAxis(final byte axisNumber) {
        switch (axisNumber) {
        case Axis.ANCESTOR:
            return new Navigator.AncestorEnumeration(this, false);
        case Axis.ANCESTOR_OR_SELF:
            return new Navigator.AncestorEnumeration(this, true);
        case Axis.ATTRIBUTE:
            return new AttributeEnumeration();
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

    private class AttributeEnumeration extends BaseEnumeration {

        private final Iterator<AttributeNode> iter = getAttributes().values().iterator();

        @Override
        public void advance() {
            if (iter.hasNext()) {
                current = iter.next();
            } else {
                current = null;
            }
        }

        @Override
        public SequenceIterator getAnother() {
            return new AttributeEnumeration();
        }
    }
}
