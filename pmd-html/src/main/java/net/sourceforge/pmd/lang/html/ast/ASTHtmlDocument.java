/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import org.jsoup.nodes.Document;

import net.sourceforge.pmd.lang.ast.RootNode;

public class ASTHtmlDocument extends ASTHtmlElement implements RootNode {

    ASTHtmlDocument(Document document) {
        super(document);
    }

    @Override
    public Object acceptVisitor(HtmlVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
