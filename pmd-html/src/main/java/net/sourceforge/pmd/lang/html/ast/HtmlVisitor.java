/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html.ast;

public interface HtmlVisitor {

    Object visit(HtmlNode node, Object data);

    Object visit(ASTHtmlCDataNode node, Object data);

    Object visit(ASTHtmlComment node, Object data);

    Object visit(ASTHtmlDocument node, Object data);

    Object visit(ASTHtmlDocumentType node, Object data);

    Object visit(ASTHtmlElement node, Object data);

    Object visit(ASTHtmlTextNode node, Object data);

    Object visit(ASTHtmlXmlDeclaration node, Object data);
}
