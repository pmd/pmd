/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.util.CollectionUtil;

import net.sf.saxon.Configuration;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.pattern.NameTest;
import net.sf.saxon.pattern.NodeTest;
import net.sf.saxon.tree.iter.AxisIterator;
import net.sf.saxon.tree.iter.EmptyIterator;
import net.sf.saxon.tree.iter.ListIterator;
import net.sf.saxon.tree.iter.ListIterator.OfNodes;
import net.sf.saxon.tree.iter.LookaheadIterator;
import net.sf.saxon.tree.iter.ReverseListIterator;
import net.sf.saxon.tree.iter.SingleNodeIterator;
import net.sf.saxon.tree.util.FastStringBuffer;
import net.sf.saxon.tree.util.Navigator;
import net.sf.saxon.tree.util.Navigator.AxisFilter;
import net.sf.saxon.tree.wrapper.AbstractNodeWrapper;
import net.sf.saxon.tree.wrapper.SiblingCountingNode;
import net.sf.saxon.type.Type;


/**
 * A wrapper for Saxon around a Node.
 */
public final class AstElementNode extends AbstractNodeWrapper implements SiblingCountingNode {

    private final AstElementNode parent;
    private final Node wrappedNode;
    /** The index of the node in the tree according to document order */
    private final int id;
    /** Name in the name pool. */
    private final int fingerprint;

    private final List<AstElementNode> children;
    private @Nullable Map<String, AstAttributeNode> attributes;


    public AstElementNode(AstDocumentNode document,
                          IdGenerator idGenerator,
                          AstElementNode parent,
                          Node wrappedNode,
                          Configuration configuration) {
        this.treeInfo = document;
        this.parent = parent;
        this.wrappedNode = wrappedNode;
        this.id = idGenerator.getNextId();
        fingerprint = configuration.getNamePool().allocateFingerprint("", wrappedNode.getXPathNodeName());

        this.children = new ArrayList<>(wrappedNode.getNumChildren());

        for (int i = 0; i < wrappedNode.getNumChildren(); i++) {
            children.add(new AstElementNode(document, idGenerator, this, wrappedNode.getChild(i), configuration));
        }
    }

    public Map<String, AstAttributeNode> makeAttributes(Node wrappedNode) {
        Map<String, AstAttributeNode> atts = new HashMap<>();
        Iterator<Attribute> it = wrappedNode.getXPathAttributesIterator();

        int attrIdx = 0;
        while (it.hasNext()) {
            Attribute next = it.next();
            atts.put(next.getName(), new AstAttributeNode(this, next, attrIdx++));
        }

        return atts;
    }

    public Map<String, AstAttributeNode> getAttributes() {
        if (attributes == null) {
            attributes = makeAttributes(getUnderlyingNode());
        }
        return attributes;
    }

    @Override
    public AstDocumentNode getTreeInfo() {
        return (AstDocumentNode) super.getTreeInfo();
    }

    List<AstElementNode> getChildren() {
        return children;
    }

    @Override
    public Node getUnderlyingNode() {
        return wrappedNode;
    }

    @Override
    public int getColumnNumber() {
        return wrappedNode.getBeginColumn();
    }

    @Override
    public int getSiblingPosition() {
        AstElementNode parent = getParent();
        return parent == null ? 0 : id - parent.id;
    }

    @Override
    public int comparePosition(NodeInfo other) {
        return super.comparePosition(other);
    }

    @Override
    public int compareOrder(NodeInfo other) {
        if (other instanceof AstElementNode) {
            return Integer.compare(this.id, ((AstElementNode) other).id);
        } else if (other instanceof SiblingCountingNode) {
            return Navigator.compareOrder(this, (SiblingCountingNode) other);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    protected AxisIterator iterateAttributes(NodeTest nodeTest) {
        if (nodeTest instanceof NameTest) {
            String local = ((NameTest) nodeTest).getLocalPart();
            return SingleNodeIterator.makeIterator(getAttributes().get(local));
        }

        return filter(nodeTest, new IteratorAdapter(getAttributes().values().iterator()));
    }


    @Override
    protected AxisIterator iterateChildren(NodeTest nodeTest) {
        return filter(nodeTest, new OfNodes(children));
    }


    @Override
    protected AxisIterator iterateSiblings(NodeTest nodeTest, boolean forwards) {
        if (parent == null) {
            return EmptyIterator.OfNodes.THE_INSTANCE;
        }

        List<? extends NodeInfo> siblingsList =
            forwards ? CollectionUtil.drop(parent.children, wrappedNode.getIndexInParent())
                     : CollectionUtil.take(parent.children, wrappedNode.getIndexInParent());

        AxisIterator iter =
            forwards ? new ListIterator.OfNodes(siblingsList)
                     : new RevListAxisIterator(siblingsList);

        return filter(nodeTest, iter);
    }

    private static AxisIterator filter(NodeTest nodeTest, AxisIterator iter) {
        return nodeTest != null ? new AxisFilter(iter, nodeTest) : iter;
    }


    @Override
    public String getAttributeValue(String uri, String local) {
        AstAttributeNode attributeWrapper = attributes.get(local);

        return attributeWrapper == null ? null : attributeWrapper.getStringValue();
    }

    public Sequence getTypedAttributeValue(String uri, String local) {
        AstAttributeNode attributeWrapper = attributes.get(local);
        return attributeWrapper == null ? null : attributeWrapper.atomize();
    }


    @Override
    protected AxisIterator iterateDescendants(NodeTest nodeTest, boolean includeSelf) {
        return filter(nodeTest, new DescendantIter(includeSelf));
    }


    @Override
    public int getLineNumber() {
        return wrappedNode.getBeginLine();
    }


    @Override
    public int getNodeKind() {
        return Type.ELEMENT;
    }


    @Override
    public NodeInfo getRoot() {
        return getTreeInfo().getRootNode();
    }


    @Override
    public void generateId(FastStringBuffer buffer) {
        buffer.append(Integer.toString(hashCode()));
    }

    @Override
    public String getLocalPart() {
        return wrappedNode.getXPathNodeName();
    }


    @Override
    public String getURI() {
        return "";
    }


    @Override
    public String getPrefix() {
        return "";
    }


    @Override
    public AstElementNode getParent() {
        return parent;
    }

    @Override
    public CharSequence getStringValueCS() {
        return getStringValue();
    }

    @Override
    public boolean hasFingerprint() {
        return true;
    }

    @Override
    public int getFingerprint() {
        return fingerprint;
    }

    @Override
    public String toString() {
        return "Wrapper[" + getLocalPart() + "]@" + hashCode();
    }

    private class DescendantIter implements AxisIterator, LookaheadIterator {

        private final Deque<AstElementNode> todo;

        public DescendantIter(boolean includeSelf) {
            todo = new ArrayDeque<>();
            if (includeSelf) {
                todo.addLast(AstElementNode.this);
            } else {
                todo.addAll(children);
            }
        }

        @Override
        public boolean hasNext() {
            return !todo.isEmpty();
        }

        @Override
        public NodeInfo next() {
            if (todo.isEmpty()) {
                return null;
            }
            AstElementNode first = todo.getFirst();
            todo.addAll(first.children);
            return first;
        }

        @Override
        public void close() {
            todo.clear();
        }

        @Override
        public int getProperties() {
            return LOOKAHEAD;
        }
    }

    private static class RevListAxisIterator extends ReverseListIterator implements AxisIterator {

        public RevListAxisIterator(List<? extends NodeInfo> list) {
            super(list);
        }

        @Override
        public NodeInfo next() {
            return (NodeInfo) super.next();
        }
    }

    private static class IteratorAdapter implements AxisIterator, LookaheadIterator {

        private final Iterator<? extends NodeInfo> it;

        public IteratorAdapter(Iterator<? extends NodeInfo> it) {
            this.it = it;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public NodeInfo next() {
            return it.hasNext() ? it.next() : null;
        }

        @Override
        public void close() {
            // nothing to do
        }


        @Override
        public int getProperties() {
            return LOOKAHEAD;
        }
    }
}
