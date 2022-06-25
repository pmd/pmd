/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import org.jsoup.nodes.Node;

import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.AbstractNodeWithTextCoordinates;

abstract class AbstractHtmlNode<T extends Node> extends AbstractNodeWithTextCoordinates<AbstractHtmlNode<?>, HtmlNode> implements HtmlNode {

    protected final T node;

    AbstractHtmlNode(T node) {
        this.node = node;
    }

    public String getNodeName() {
        return node.nodeName();
    }

    @Override
    public String getXPathNodeName() {
        return node.nodeName();
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

    @Override
    protected void setCoords(int bline, int bcol, int eline, int ecol) {
        super.setCoords(bline, bcol, eline, ecol);
    }
}
