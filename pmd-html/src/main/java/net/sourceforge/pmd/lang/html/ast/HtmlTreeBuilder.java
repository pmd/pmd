/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import java.util.Map;

import org.jsoup.nodes.CDataNode;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.nodes.XmlDeclaration;

import net.sourceforge.pmd.lang.ast.Parser;

final class HtmlTreeBuilder {

    public ASTHtmlDocument build(Document doc,
                                 Parser.ParserTask task,
                                 Map<Integer, String> suppressMap) {
        ASTHtmlDocument root = new ASTHtmlDocument(doc, task, suppressMap);
        addChildren(root, doc);

        LineNumbers lineNumbers = new LineNumbers(root);
        lineNumbers.determine();

        return root;
    }
    
    private void addChildren(AbstractHtmlNode<?> parent, Node node) {
        for (Node child : node.childNodes()) {
            AbstractHtmlNode<?> converted = convertJsoupNode(child);
            parent.addChild(converted, parent.getNumChildren());
            addChildren(converted, child);
        }
    }

    private AbstractHtmlNode<? extends Node> convertJsoupNode(Node node) {
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
