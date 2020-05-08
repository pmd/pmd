/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.rule.xpath.internal.AstElementNode.DescendantIter;

import net.sf.saxon.Configuration;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.pattern.NodeTest;
import net.sf.saxon.tree.iter.AxisIterator;
import net.sf.saxon.tree.iter.EmptyIterator;
import net.sf.saxon.tree.iter.ListIterator;
import net.sf.saxon.tree.util.FastStringBuffer;
import net.sf.saxon.type.Type;

/**
 *
 */
class AstDocumentNode extends BaseNodeInfo {

    private final AstElementNode rootElement;
    private final List<AstElementNode> children;

    AstDocumentNode(AstTreeInfo document,
                    IdGenerator idGenerator,
                    RootNode wrappedNode,
                    Configuration configuration) {
        super(Type.DOCUMENT, configuration.getNamePool(), "", null);
        this.rootElement = new AstElementNode(document, idGenerator, this, wrappedNode, configuration);
        this.children = Collections.singletonList(rootElement);
    }

    @Override
    List<AstElementNode> getChildren() {
        return children;
    }

    public AstElementNode getRootElement() {
        return rootElement;
    }

    @Override
    protected AxisIterator iterateAttributes(NodeTest nodeTest) {
        return EmptyIterator.OfNodes.THE_INSTANCE;
    }

    @Override
    protected AxisIterator iterateChildren(NodeTest nodeTest) {
        return filter(nodeTest, new ListIterator.OfNodes.OfNodes(children));
    }

    @Override
    protected AxisIterator iterateSiblings(NodeTest nodeTest, boolean forwards) {
        return EmptyIterator.OfNodes.THE_INSTANCE;
    }

    @Override
    protected AxisIterator iterateDescendants(NodeTest nodeTest, boolean includeSelf) {
        return filter(nodeTest, new DescendantIter(this, includeSelf));
    }

    @Override
    public int getSiblingPosition() {
        return 0;
    }

    @Override
    public Node getUnderlyingNode() {
        return rootElement.getUnderlyingNode();
    }

    @Override
    public int compareOrder(NodeInfo other) {
        return other == this ? 0 : -1;
    }

    @Override
    public String getLocalPart() {
        return "";
    }

    @Override
    public void generateId(FastStringBuffer buffer) {
        buffer.append("0");
    }

    @Override
    public CharSequence getStringValueCS() {
        return "";
    }
}
