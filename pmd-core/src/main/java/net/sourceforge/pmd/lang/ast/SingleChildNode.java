/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;


/**
 * Interface for a node that only has children of a specific type T.
 * The return type of some methods may then be specialized. TODO implement
 * that on more nodes.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface SingleChildNode<T extends Node> extends Node {


    @Override
    T jjtGetChild(int index);


    @Override
    NodeStream<T> childrenStream();
}
