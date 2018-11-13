/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.QualifiableNode;


/**
 * Java nodes that can be described with a qualified name.
 *
 * @author Clément Fournier
 */
public interface JavaQualifiableNode extends QualifiableNode {

    /**
     * Returns a qualified name for this node.
     *
     * @return A qualified name.
     */
    @Override
    JavaQualifiedName getQualifiedName();
}
