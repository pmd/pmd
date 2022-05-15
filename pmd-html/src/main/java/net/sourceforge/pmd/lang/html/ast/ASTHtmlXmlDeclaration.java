/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import org.jsoup.nodes.XmlDeclaration;

public final class ASTHtmlXmlDeclaration extends AbstractHtmlNode<XmlDeclaration> {

    ASTHtmlXmlDeclaration(XmlDeclaration node) {
        super(node);
    }

    @Override
    protected <P, R> R acceptHtmlVisitor(HtmlVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getName() {
        return node.name();
    }
}
