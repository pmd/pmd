/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import org.jsoup.nodes.Comment;

public final class HtmlComment extends AbstractHtmlNode<Comment> {

    HtmlComment(Comment node) {
        super(node);
    }

    public String getData() {
        return node.getData();
    }
}
