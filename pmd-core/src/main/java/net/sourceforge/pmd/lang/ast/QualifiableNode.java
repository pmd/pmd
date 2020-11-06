/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

/**
 * Nodes that can be described with a qualified name.
 *
 * @author Clément Fournier
 *
 * @deprecated Not useful anymore
 */
@Deprecated
public interface QualifiableNode extends Node {

    /**
     * Returns a qualified name for this node.
     *
     * @return A qualified name.
     */
    QualifiedName getQualifiedName();

}
