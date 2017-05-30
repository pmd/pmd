/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Nodes that can be described with a qualified name.
 *
 * @author Clément Fournier
 */
public interface QualifiableNode {

    /**
     * Returns a qualified name for this node.
     *
     * @return A qualified name.
     */
    QualifiedName getQualifiedName();

}
