/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import org.jsoup.nodes.TextNode;

public class ASTHtmlTextNode extends AbstractHtmlNode<TextNode> implements net.sourceforge.pmd.lang.rule.xpath.TextNode {

    ASTHtmlTextNode(TextNode node) {
        super(node);
    }

    @Override
    protected <P, R> R acceptHtmlVisitor(HtmlVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getWholeText() {
        return node.getWholeText();
    }

    @Override
    public String getText() {
        return node.text();
    }

    @Override
    public String getXPathNodeName() {
        return net.sourceforge.pmd.lang.rule.xpath.TextNode.super.getXPathNodeName();
    }
}
