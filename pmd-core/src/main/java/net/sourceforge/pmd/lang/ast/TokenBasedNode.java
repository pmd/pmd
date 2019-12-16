/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

/**
 * A {@link Node} that can provide access to the underlying
 * {@linkplain GenericToken tokens} produced by the lexer.
 */
public interface TokenBasedNode<T extends GenericToken> extends Node {

    /**
     * Returns the first token producing this node.
     * This is not a special token.
     */
    T getFirstToken();


    /**
     * Returns the last token producing this node.
     * This is not a special token.
     */
    T getLastToken();


}
