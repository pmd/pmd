/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import org.jsoup.nodes.CDataNode;

public final class HtmlCDataNode extends AbstractHtmlNode<CDataNode> {

    HtmlCDataNode(CDataNode node) {
        super(node);
    }

    public String getText() {
        return node.text();
    }

    @Override
    public Object acceptVisitor(HtmlVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
