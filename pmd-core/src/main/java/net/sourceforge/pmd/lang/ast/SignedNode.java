/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;


/**
 * Nodes that can be described by a signature.
 *
 * @param <T> The type of the signature
 *
 * @author Clément Fournier
 */
public interface SignedNode<T> extends Node {

    /**
     * Gets the signature of this node.
     *
     * @return The signature
     */
    T getSignature();

}
