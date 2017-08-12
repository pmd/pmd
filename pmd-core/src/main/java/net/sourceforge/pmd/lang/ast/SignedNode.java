/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;


import net.sourceforge.pmd.lang.metrics.Signature;

/**
 * Nodes that can be described by a signature.
 *
 * @param <N> The type of node
 *
 * @author Clément Fournier
 */
public interface SignedNode<N> extends Node {

    /**
     * Gets the signature of this node.
     *
     * @return The signature
     */
    Signature<? super N> getSignature();

}
