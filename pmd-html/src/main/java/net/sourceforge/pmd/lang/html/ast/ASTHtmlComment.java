/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import org.jsoup.nodes.Comment;

public final class ASTHtmlComment extends AbstractHtmlNode<Comment> {

    ASTHtmlComment(Comment node) {
        super(node);
    }

    public String getData() {
        return node.getData();
    }

    @Override
    public Object acceptVisitor(HtmlVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
