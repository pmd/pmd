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
    public Object visit(HtmlCDataNode node, Object data) {
        return visit((HtmlNode) node, data);
    }

    @Override
    public Object visit(HtmlComment node, Object data) {
        return visit((HtmlNode) node, data);
    }

    @Override
    public Object visit(HtmlDocument node, Object data) {
        return visit((HtmlNode) node, data);
    }

    @Override
    public Object visit(HtmlDocumentType node, Object data) {
        return visit((HtmlNode) node, data);
    }

    @Override
    public Object visit(HtmlElement node, Object data) {
        return visit((HtmlNode) node, data);
    }

    @Override
    public Object visit(HtmlTextNode node, Object data) {
        return visit((HtmlNode) node, data);
    }

    @Override
    public Object visit(HtmlXmlDeclaration node, Object data) {
        return visit((HtmlNode) node, data);
    }
}
