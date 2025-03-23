/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;

import net.sf.saxon.Configuration;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.pattern.NodeTest;
import net.sf.saxon.tree.iter.AxisIterator;
import net.sf.saxon.tree.iter.EmptyIterator;
import net.sf.saxon.type.Type;

/**
 *  See {@link AstTreeInfo#getRootNode()}.
 */
class AstDocumentNode extends BaseNodeInfo implements AstNodeOwner {

    private final AstElementNode rootElement;
    private final List<AstElementNode> children;

    AstDocumentNode(AstTreeInfo document,
                    MutableInt idGenerator,
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
        return EmptyIterator.ofNodes();
    }

    @Override
    protected AxisIterator iterateChildren(NodeTest nodeTest) {
        return filter(nodeTest, iterateList(children));
    }

    @Override
    protected AxisIterator iterateSiblings(NodeTest nodeTest, boolean forwards) {
        return EmptyIterator.ofNodes();
    }

    @Override
    public int getSiblingPosition() {
        return 0;
    }

    @Override
    public Node getUnderlyingNode() {
        // this is a concession to the model, so that the expression "/"
        // may be interpreted as the root node
        return rootElement.getUnderlyingNode();
    }

    @Override
    public int compareOrder(NodeInfo other) {
        return other == this ? 0 : -1; // NOPMD CompareObjectsWithEquals - only a single root per tree
    }

    @Override
    public boolean hasChildNodes() {
        return true;
    }

    @Override
    public String getLocalPart() {
        return "";
    }

    @Override
    public void generateId(StringBuilder buffer) {
        buffer.append("0");
    }

    @Override
    public String getStringValue() {
        return "";
    }
}
