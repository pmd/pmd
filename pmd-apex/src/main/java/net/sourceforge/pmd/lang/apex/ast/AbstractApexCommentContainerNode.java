/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;

/**
 * Abstract base class for nodes which can contain comments.
 *
 * @param <T> the node type
 */
abstract class AbstractApexCommentContainerNode<T extends Node> extends AbstractApexNode<T> implements ASTCommentContainer<T> {

    private boolean containsComment = false;

    protected AbstractApexCommentContainerNode(T node) {
        super(node);
    }

    void setContainsComment(boolean containsComment) {
        this.containsComment = containsComment;
    }

    @Override
    public boolean getContainsComment() {
        return containsComment;
    }
}
