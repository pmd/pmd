/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.NodeStream;

import apex.jorje.semantic.ast.AstNode;

/**
 * @author Clément Fournier
 */
public interface ASTUserClassOrInterface<T extends AstNode> extends ApexQualifiableNode, ApexNode<T> {

    /**
     * Finds the type kind of this declaration.
     *
     * @return The type kind of this declaration.
     */
    TypeKind getTypeKind();


    /**
     * Returns the (non-synthetic) methods defined in this type.
     */
    @NonNull
    default NodeStream<ASTMethod> getMethods() {
        return children(ASTMethod.class).filterNot(it -> it.getImage().matches("(<clinit>|<init>|clone)"));
    }


    /**
     * The kind of type this node declares.
     */
    enum TypeKind {
        CLASS, INTERFACE
    }


}
