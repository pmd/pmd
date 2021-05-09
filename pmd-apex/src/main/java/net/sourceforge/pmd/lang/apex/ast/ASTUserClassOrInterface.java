/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.NodeStream;

import apex.jorje.semantic.ast.AstNode;

/**
 * An Apex type declaration.
 *
 * @author Clément Fournier
 */
public interface ASTUserClassOrInterface<T extends AstNode> extends ApexQualifiableNode, ApexNode<T> {

    /** Return the simple name of the type defined by this node. */
    String getSimpleName();

    /**
     * Return the modifier node for this type declaration.
     */
    default ASTModifierNode getModifiers() {
        return firstChild(ASTModifierNode.class);
    }

    /**
     * Returns the (non-synthetic) methods defined in this type.
     */
    default @NonNull NodeStream<ASTMethod> getMethods() {
        return children(ASTMethod.class).filterNot(it -> it.getImage().matches("(<clinit>|<init>|clone)"));
    }


}
