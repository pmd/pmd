/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Java nodes that can be described with a qualified name.
 *
 * @author Clément Fournier
 *
 * @deprecated See {@link JavaQualifiedName}
 */
@Deprecated
public interface JavaQualifiableNode extends JavaNode {

    /**
     * Returns a qualified name for this node.
     *
     * @return A qualified name.
     */
    JavaQualifiedName getQualifiedName();
}
