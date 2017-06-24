/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Groups enums, classes and interface declarations.
 *
 * @author Clément Fournier
 */
public interface ASTAnyTypeDeclaration extends QualifiableNode, AccessNode, JavaNode {

    /**
     * Finds the type kind of this declaration.
     *
     * @return The type kind of this declaration.
     */
    TypeKind getTypeKind();

    enum TypeKind {
        CLASS, INTERFACE, ENUM
    }

}
