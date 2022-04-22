/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html.ast;

public class HtmlVisitorAdapter implements HtmlVisitor {

    @Override
    public Object visit(HtmlNode node, Object data) {
        for (HtmlNode child : node.children()) {
            child.acceptVisitor(this, data);
        }
        return null;
    }

    @Override
    public Object visit(ASTHtmlCDataNode node, Object data) {
        return visit((HtmlNode) node, data);
    }

    @Override
    public Object visit(ASTHtmlComment node, Object data) {
        return visit((HtmlNode) node, data);
    }

    @Override
    public Object visit(ASTHtmlDocument node, Object data) {
        return visit((HtmlNode) node, data);
    }

    @Override
    public Object visit(ASTHtmlDocumentType node, Object data) {
        return visit((HtmlNode) node, data);
    }

    @Override
    public Object visit(ASTHtmlElement node, Object data) {
        return visit((HtmlNode) node, data);
    }

    @Override
    public Object visit(ASTHtmlTextNode node, Object data) {
        return visit((HtmlNode) node, data);
    }

    @Override
    public Object visit(ASTHtmlXmlDeclaration node, Object data) {
        return visit((HtmlNode) node, data);
    }
}
