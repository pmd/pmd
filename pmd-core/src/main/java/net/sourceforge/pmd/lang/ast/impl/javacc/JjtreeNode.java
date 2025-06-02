/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.TextAvailableNode;
import net.sourceforge.pmd.lang.ast.impl.GenericNode;

/**
 * Base interface for nodes that are produced by a JJTree parser. Our
 * JJTree implementation gives {@link TextAvailableNode} for free, access
 * to tokens is also guaranteed.
 *
 * @param <N> Self type
 */
public interface JjtreeNode<N extends JjtreeNode<N>> extends GenericNode<N>, TextAvailableNode {


    // todo token accessors should most likely be protected in PMD 7.

    JavaccToken getFirstToken();


    JavaccToken getLastToken();

    /**
     * Returns a token range, that includes the first and last token.
     */
    default Iterable<JavaccToken> tokens() {
        return GenericToken.range(getFirstToken(), getLastToken());
    }

}
