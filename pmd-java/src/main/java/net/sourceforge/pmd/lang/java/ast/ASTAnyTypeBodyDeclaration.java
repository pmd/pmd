/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Marker interface for type body declarations, such as annotation members, field or method declarations.
 *
 * @author Clément Fournier
 *
 * @deprecated This type and subtypes are removed from the tree
 */
@Deprecated
public interface ASTAnyTypeBodyDeclaration extends JavaNode {


    /**
     * Returns the child of this declaration,
     * which can be cast to a more specific node
     * type using #getKind() as a cue.
     *
     * <p>Returns null if this is an empty declaration,
     * that is, a single semicolon.
     */
    default JavaNode getDeclarationNode() {
        return this;
    }


}
