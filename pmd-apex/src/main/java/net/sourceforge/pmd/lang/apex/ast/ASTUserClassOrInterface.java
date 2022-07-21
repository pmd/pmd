/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;

/**
 * @author Clément Fournier
 */
public interface ASTUserClassOrInterface<T extends Node> extends ApexQualifiableNode, ApexNode<T> {

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
