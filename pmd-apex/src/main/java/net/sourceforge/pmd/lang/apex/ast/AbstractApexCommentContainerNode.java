/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.AstNode;

/**
 * Abstract base class for nodes which can contain comments.
 *
 * @param <T> the node type
 */
public abstract class AbstractApexCommentContainerNode<T extends AstNode> extends AbstractApexNode<T> implements ASTCommentContainer<T> {

    private boolean containsComment = false;

    protected AbstractApexCommentContainerNode(T node) {
        super(node);
    }

    @Override
    public void setContainsComment(boolean containsComment) {
        this.containsComment = containsComment;
    }

    @Override
    public boolean getContainsComment() {
        return containsComment;
    }
}
