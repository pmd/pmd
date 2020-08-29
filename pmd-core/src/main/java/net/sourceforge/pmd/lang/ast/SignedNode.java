/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;


import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.metrics.Signature;

/**
 * Nodes that can be described by a signature.
 *
 * @param <N> The type of node
 *
 * @author Clément Fournier
 */
@Deprecated
@DeprecatedUntil700
public interface SignedNode<N> extends Node {

    /**
     * Gets the signature of this node.
     *
     * @return The signature
     */
    @Deprecated
    @DeprecatedUntil700
    Signature<? super N> getSignature();

}
