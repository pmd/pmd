/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html.ast;

import net.sourceforge.pmd.lang.ast.AstVisitor;

public interface HtmlVisitor<P, R> extends AstVisitor<P, R> {

    /**
     * The default visit method, to which other methods delegate.
     */
    default R visitHtmlNode(HtmlNode node, P data) {
        return visitNode(node, data);
    }

    default R visit(ASTHtmlCDataNode node, P data) {
        return visitHtmlNode(node, data);
    }

    default R visit(ASTHtmlComment node, P data) {
        return visitHtmlNode(node, data);
    }

    default R visit(ASTHtmlDocument node, P data) {
        return visitHtmlNode(node, data);
    }

    default R visit(ASTHtmlDocumentType node, P data) {
        return visitHtmlNode(node, data);
    }

    default R visit(ASTHtmlElement node, P data) {
        return visitHtmlNode(node, data);
    }

    default R visit(ASTHtmlTextNode node, P data) {
        return visitHtmlNode(node, data);
    }

    default R visit(ASTHtmlXmlDeclaration node, P data) {
        return visitHtmlNode(node, data);
    }
}
