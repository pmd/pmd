/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import org.jsoup.nodes.TextNode;

public class ASTHtmlTextNode extends AbstractHtmlNode<TextNode> {

    ASTHtmlTextNode(TextNode node) {
        super(node);
    }

    @Override
    protected <P, R> R acceptHtmlVisitor(HtmlVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getNormalizedText() {
        return node.text();
    }

    public String getText() {
        return node.getWholeText();
    }

    @Override
    public String getImage() {
        return getNormalizedText();
    }
}
