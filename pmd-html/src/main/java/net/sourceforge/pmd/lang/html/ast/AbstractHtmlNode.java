/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import org.jsoup.nodes.Node;

import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.AbstractNode;
import net.sourceforge.pmd.lang.document.TextRegion;

abstract class AbstractHtmlNode<T extends Node> extends AbstractNode<AbstractHtmlNode<?>, HtmlNode> implements HtmlNode {

    protected final T node;
    protected int startOffset;
    protected int endOffset;

    AbstractHtmlNode(T node) {
        this.node = node;
    }

    public String getNodeName() {
        return node.nodeName();
    }

    @Override
    public String getXPathNodeName() {
        // note: this might return "#text" or "#comment" as well
        return node.nodeName();
    }

    @Override
    public TextRegion getTextRegion() {
        return TextRegion.fromBothOffsets(startOffset, endOffset);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <P, R> R acceptVisitor(AstVisitor<? super P, ? extends R> visitor, P data) {
        if (visitor instanceof HtmlVisitor) {
            return this.acceptHtmlVisitor((HtmlVisitor<? super P, ? extends R>) visitor, data);
        }
        return visitor.cannotVisit(this, data);
    }

    protected abstract <P, R> R acceptHtmlVisitor(HtmlVisitor<? super P, ? extends R> visitor, P data);

    // overridden to make them visible
    @Override
    protected void addChild(AbstractHtmlNode<?> child, int index) {
        super.addChild(child, index);
    }

}
