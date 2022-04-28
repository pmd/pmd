/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import org.jsoup.nodes.CDataNode;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.nodes.XmlDeclaration;

final class HtmlTreeBuilder {

    public ASTHtmlDocument build(Document doc, String htmlString) {
        ASTHtmlDocument root = new ASTHtmlDocument(doc);
        addChildren(root, doc);

        LineNumbers lineNumbers = new LineNumbers(root, htmlString);
        lineNumbers.determine();

        return root;
    }
    
    private void addChildren(HtmlNode parent, Node node) {
        for (Node child : node.childNodes()) {
            HtmlNode converted = convertJsoupNode(child);
            parent.jjtAddChild(converted, parent.getNumChildren());
            addChildren(converted, child);
        }
    }

    private HtmlNode convertJsoupNode(Node node) {
        if (node instanceof Element) {
            return new ASTHtmlElement((Element) node);
        } else if (node instanceof CDataNode) {
            return new ASTHtmlCDataNode((CDataNode) node);
        } else if (node instanceof TextNode) {
            return new ASTHtmlTextNode((TextNode) node);
        } else if (node instanceof Comment) {
            return new ASTHtmlComment((Comment) node);
        } else if (node instanceof XmlDeclaration) {
            return new ASTHtmlXmlDeclaration((XmlDeclaration) node);
        } else if (node instanceof DocumentType) {
            return new ASTHtmlDocumentType((DocumentType) node);
        } else {
            throw new RuntimeException("Unsupported node type: " + node.getClass());
        }
    }
}
