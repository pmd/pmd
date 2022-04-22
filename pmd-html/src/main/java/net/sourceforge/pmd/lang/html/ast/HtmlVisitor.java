/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html.ast;

public interface HtmlVisitor {

    Object visit(HtmlNode node, Object data);

    Object visit(HtmlCDataNode node, Object data);

    Object visit(HtmlComment node, Object data);

    Object visit(HtmlDocument node, Object data);

    Object visit(HtmlDocumentType node, Object data);

    Object visit(HtmlElement node, Object data);

    Object visit(HtmlTextNode node, Object data);

    Object visit(HtmlXmlDeclaration node, Object data);
}
