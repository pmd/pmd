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
    public Object acceptVisitor(HtmlVisitor visitor, Object data) {
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
