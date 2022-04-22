/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import org.jsoup.nodes.Node;

import net.sourceforge.pmd.lang.ast.AbstractNode;

abstract class AbstractHtmlNode<T extends Node> extends AbstractNode implements HtmlNode {

    protected final T node;

    AbstractHtmlNode(T node) {
        super(0);
        this.node = node;
    }

    public String getNodeName() {
        return node.nodeName();
    }

    @Override
    public String getXPathNodeName() {
        return node.nodeName();
    }

    @Override
    public Iterable<? extends HtmlNode> children() {
        return (Iterable<? extends HtmlNode>) super.children();
    }

    void setBeginLine(int beginLine) {
        this.beginLine = beginLine;
    }

    void setBeginColumn(int beginColumn) {
        this.beginColumn = beginColumn;
    }

    void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    void setEndColumn(int endColumn) {
        this.endColumn = endColumn;
    }
}
