/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration.TypeKind;

/**
 * Marker interface for type body declarations, such as annotation members, field or method declarations.
 *
 * @author Clément Fournier
 */
public interface ASTAnyTypeBodyDeclaration extends JavaNode {


    /**
     * Returns the child of this declaration,
     * which can be cast to a more specific node
     * type using {@link #getKind()} as a cue.
     *
     * <p>Returns null if this is an empty declaration,
     * that is, a single semicolon.
     */
    JavaNode getDeclarationNode();


    /**
     * Gets the kind of declaration this node contains.
     * This is a cue for the node type the child of this
     * declaration can be cast to.
     */
    DeclarationKind getKind();


    /**
     * Kind of declaration. This is not deprecated because the node will
     * go away entirely in 7.0.0 and one cannot avoid using it on master.
     * See {@link TypeKind} for the reasons for deprecation.
     */
    enum DeclarationKind {
        /** See {@link ASTInitializer}. */
        INITIALIZER,
        /** See {@link ASTConstructorDeclaration}. */
        CONSTRUCTOR,
        /** See {@link ASTMethodDeclaration}. */
        METHOD,
        /** See {@link ASTFieldDeclaration}. */
        FIELD,
        /** See {@link ASTAnnotationMethodDeclaration}. */
        ANNOTATION_METHOD,
        /** See {@link ASTClassOrInterfaceDeclaration}. */
        CLASS,
        /** See {@link ASTEnumDeclaration}. */
        ENUM,
        /** See {@link ASTClassOrInterfaceDeclaration}. */
        INTERFACE,
        /** See {@link ASTAnnotationTypeDeclaration}. */
        ANNOTATION,
        /** No child, {@link #getDeclarationNode()} will return null. */
        EMPTY,
        /** See {@link ASTRecordDeclaration}. */
        RECORD,
        /** See {@link ASTRecordConstructorDeclaration}. */
        RECORD_CONSTRUCTOR
    }

}
