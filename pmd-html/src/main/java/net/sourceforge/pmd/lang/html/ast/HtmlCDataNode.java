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
}
