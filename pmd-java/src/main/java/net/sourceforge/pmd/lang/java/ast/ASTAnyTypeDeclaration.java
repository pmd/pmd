/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

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


    /**
     * Retrieves the member declarations (fields, methods, classes, etc.) from the body of this type declaration.
     *
     * @return The member declarations declared in this type declaration
     */
    List<ASTAnyTypeBodyDeclaration> getDeclarations();


    /**
     * The kind of type this node declares.
     */
    enum TypeKind {
        CLASS, INTERFACE, ENUM, ANNOTATION
    }

}
