/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableInt;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.CommentNode;
import net.sourceforge.pmd.lang.rule.xpath.TextNode;
import net.sourceforge.pmd.util.CollectionUtil;

import net.sf.saxon.Configuration;
import net.sf.saxon.om.NamespaceUri;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.pattern.NameTest;
import net.sf.saxon.pattern.NodeTest;
import net.sf.saxon.tree.iter.AxisIterator;
import net.sf.saxon.tree.iter.EmptyIterator;
import net.sf.saxon.tree.iter.LookaheadIterator;
import net.sf.saxon.tree.iter.SingleNodeIterator;
import net.sf.saxon.tree.util.Navigator;
import net.sf.saxon.tree.wrapper.SiblingCountingNode;
import net.sf.saxon.type.Type;


/**
 * A wrapper for Saxon around a Node. Note: the {@link RootNode} of a tree
 * corresponds to both a document node and an element node that is its child.
 */
public final class AstElementNode extends BaseNodeInfo implements SiblingCountingNode, AstNodeOwner {

    private final Node wrappedNode;
    /** The index of the node in the tree according to document order */
    private final int id;

    private final List<AstElementNode> children;
    private @Nullable Map<String, AstAttributeNode> attributes;
    private @Nullable Map<String, Attribute> lightAttributes;


    AstElementNode(AstTreeInfo document,
                   MutableInt idGenerator,
                   BaseNodeInfo parent,
                   Node wrappedNode,
                   Configuration configuration) {
        super(determineType(wrappedNode), configuration.getNamePool(), wrappedNode.getXPathNodeName(), parent);

        this.treeInfo = document;
        this.wrappedNode = wrappedNode;
        this.id = idGenerator.getAndIncrement();

        this.children = new ArrayList<>(wrappedNode.getNumChildren());

        for (int i = 0; i < wrappedNode.getNumChildren(); i++) {
            children.add(new AstElementNode(document, idGenerator, this, wrappedNode.getChild(i), configuration));
        }
    }

    private static int determineType(Node node) {
        // As of PMD 7, only the HTML module uses these interfaces
        if (node instanceof TextNode) {
            return Type.TEXT;
        } else if (node instanceof CommentNode) {
            return Type.COMMENT;
        }
        return Type.ELEMENT;
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

    public Map<String, Attribute> getLightAttributes() {
        if (lightAttributes == null) {
            lightAttributes = new HashMap<>();
            getUnderlyingNode().getXPathAttributesIterator()
                               .forEachRemaining(it -> lightAttributes.put(it.getName(), it));
        }
        return lightAttributes;
    }

    @Override
    public boolean hasChildNodes() {
        return !children.isEmpty();
    }

    @Override
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
        BaseNodeInfo parent = getParent();
        return !(parent instanceof AstElementNode) ? 0
                                                   : id - ((AstElementNode) parent).id;
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
    protected AxisIterator iterateAttributes(NodeTest predicate) {
        if (predicate instanceof NameTest) {
            String local = ((NameTest) predicate).getLocalPart();
            return SingleNodeIterator.makeIterator(getAttributes().get(local));
        }

        return filter(predicate, new IteratorAdapter(getAttributes().values().iterator()));
    }

    @Override
    protected AxisIterator iterateChildren(NodeTest nodeTest) {
        return filter(nodeTest, iterateList(children));
    }

    @Override // this excludes self
    protected AxisIterator iterateSiblings(NodeTest nodeTest, boolean forwards) {
        if (parent == null) {
            return EmptyIterator.ofNodes();
        }

        List<? extends NodeInfo> siblingsList =
            forwards ? CollectionUtil.drop(parent.getChildren(), wrappedNode.getIndexInParent() + 1)
                     : CollectionUtil.take(parent.getChildren(), wrappedNode.getIndexInParent());

        return filter(nodeTest, iterateList(siblingsList, forwards));
    }

    @Override
    public String getAttributeValue(NamespaceUri uri, String local) {
        Attribute attribute = getLightAttributes().get(local);
        if (attribute != null) {
            getTreeInfo().getLogger().recordUsageOf(attribute);
            return attribute.getStringValue();
        }
        return null;
    }


    @Override
    public int getLineNumber() {
        return wrappedNode.getBeginLine();
    }


    @Override
    public NodeInfo getRoot() {
        return getTreeInfo().getRootNode();
    }


    @Override
    public void generateId(StringBuilder buffer) {
        buffer.append(id);
    }

    @Override
    public String getLocalPart() {
        return wrappedNode.getXPathNodeName();
    }

    @Override
    public String getStringValue() {
        Node node = getUnderlyingNode();
        if (node instanceof TextNode) {
            return ((TextNode) node).getText();
        }
        if (node instanceof CommentNode) {
            return ((CommentNode) node).getData();
        }

        // https://www.w3.org/TR/xpath-datamodel-31/#ElementNode
        // The string-value property of an Element Node must be the
        // concatenation of the string-values of all its Text Node
        // descendants in document order or, if the element has no such
        // descendants, the zero-length string.

        // Since we represent all our Nodes as elements, there are no
        // text nodes usually, except for HTML module - there we have
        // potentially text nodes
        return node.descendants(TextNode.class).toStream()
                .map(TextNode::getText)
                .collect(Collectors.joining(""));
    }

    @Override
    public String toString() {
        return "Wrapper[" + getLocalPart() + "]@" + hashCode();
    }



    private static class IteratorAdapter implements AxisIterator, LookaheadIterator {

        private final Iterator<? extends NodeInfo> it;

        IteratorAdapter(Iterator<? extends NodeInfo> it) {
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
        public boolean supportsHasNext() {
            return true;
        }
    }
}
