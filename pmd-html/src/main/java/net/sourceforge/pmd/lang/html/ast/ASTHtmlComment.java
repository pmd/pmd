/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import org.jsoup.nodes.Comment;

import net.sourceforge.pmd.lang.rule.xpath.CommentNode;

public final class ASTHtmlComment extends AbstractHtmlNode<Comment> implements CommentNode {

    ASTHtmlComment(Comment node) {
        super(node);
    }

    @Override
    public String getData() {
        return node.getData();
    }

    @Override
    protected <P, R> R acceptHtmlVisitor(HtmlVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getXPathNodeName() {
        return CommentNode.super.getXPathNodeName();
    }
}
