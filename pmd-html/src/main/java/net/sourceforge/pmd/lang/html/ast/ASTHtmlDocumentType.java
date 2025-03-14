/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import org.jsoup.nodes.DocumentType;

public final class ASTHtmlDocumentType extends AbstractHtmlNode<DocumentType> {

    ASTHtmlDocumentType(DocumentType node) {
        super(node);
    }

    @Override
    protected <P, R> R acceptHtmlVisitor(HtmlVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getName() {
        return node.name();
    }

    public String getPublicId() {
        return node.publicId();
    }

    public String getSystemId() {
        return node.systemId();
    }
}
