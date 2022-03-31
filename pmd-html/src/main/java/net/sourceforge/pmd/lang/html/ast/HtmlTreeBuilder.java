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

import net.sourceforge.pmd.annotation.InternalApi;

@InternalApi
public final class HtmlTreeBuilder {

    public HtmlDocument build(Document doc, String htmlString) {
        HtmlDocument root = new HtmlDocument(doc);
        addChilds(root, doc);

        LineNumbers lineNumbers = new LineNumbers(root, htmlString);
        lineNumbers.determine();

        return root;
    }
    
    private void addChilds(HtmlNode parent, Node node) {
        for (Node child : node.childNodes()) {
            HtmlNode converted = convertJsoupNode(child);
            parent.jjtAddChild(converted, parent.getNumChildren());
            addChilds(converted, child);
        }
    }

    private HtmlNode convertJsoupNode(Node node) {
        if (node instanceof Element) {
            return new HtmlElement((Element) node);
        } else if (node instanceof CDataNode) {
            return new HtmlCDataNode((CDataNode) node);
        } else if (node instanceof TextNode) {
            return new HtmlTextNode((TextNode) node);
        } else if (node instanceof Comment) {
            return new HtmlComment((Comment) node);
        } else if (node instanceof XmlDeclaration) {
            return new HtmlXmlDeclaration((XmlDeclaration) node);
        } else if (node instanceof DocumentType) {
            return new HtmlDocumentType((DocumentType) node);
        } else {
            throw new RuntimeException("Unsupported node type: " + node.getClass());
        }
    }
}
