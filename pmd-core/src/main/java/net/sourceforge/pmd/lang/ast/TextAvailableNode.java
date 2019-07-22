/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

/**
 * Refinement of {@link Node} for those nodes that can provide access
 * to the source text.
 *
 * @since 7.0.0
 */
public interface TextAvailableNode extends Node {

    /**
     * Returns the original source underlying this node. In particular,
     * for a {@link RootNode}, returns the whole text of the file.
     */
    String getText();


    int getStartOffset();


    int getEndOffset();


}
