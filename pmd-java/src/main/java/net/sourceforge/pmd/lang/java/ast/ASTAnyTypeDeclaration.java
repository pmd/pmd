/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;


/**
 * Groups enum, class, annotation and interface declarations.
 *
 * @author Clément Fournier
 */
public interface ASTAnyTypeDeclaration extends TypeNode, JavaQualifiableNode, AccessNode, JavaNode {

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



    @Override
    JavaTypeQualifiedName getQualifiedName();


    /**
     * Returns true if this type declaration is nested inside an interface, class or annotation.
     */
    boolean isNested();

    /**
     * The kind of type this node declares.
     */
    enum TypeKind {
        CLASS, INTERFACE, ENUM, ANNOTATION
    }

}
