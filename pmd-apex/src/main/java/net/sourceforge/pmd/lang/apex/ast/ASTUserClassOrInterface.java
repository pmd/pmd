/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

/**
 * @author Clément Fournier
 *
 * @param <T> placeholder
 */
public interface ASTUserClassOrInterface<T> extends ApexQualifiableNode, ApexNode<Void> {

    /**
     * Finds the type kind of this declaration.
     *
     * @return The type kind of this declaration.
     */
    TypeKind getTypeKind();


    /**
     * The kind of type this node declares.
     */
    enum TypeKind {
        CLASS, INTERFACE
    }


}
