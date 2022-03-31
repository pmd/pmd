/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import org.jsoup.nodes.TextNode;

public class HtmlTextNode extends AbstractHtmlNode<TextNode> {

    HtmlTextNode(TextNode node) {
        super(node);
    }

    public String getNormalizedText() {
        return node.text();
    }

    public String getText() {
        return node.getWholeText();
    }

    @Override
    public String getImage() {
        return getNormalizedText();
    }
}
