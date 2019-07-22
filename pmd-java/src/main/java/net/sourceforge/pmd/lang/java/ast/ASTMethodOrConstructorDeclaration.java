/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.SignedNode;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSignature;


/**
 * Groups method and constructor declarations under a common type.
 *
 * @author Clément Fournier
 * @see MethodLikeNode
 * @since 5.8.1
 */
public interface ASTMethodOrConstructorDeclaration extends MethodLikeNode, SignedNode<ASTMethodOrConstructorDeclaration> {
    @Override
    JavaOperationSignature getSignature();


    /**
     * Returns the formal parameters node of this method or constructor.
     */
    @NonNull
    default ASTFormalParameters getFormalParameters() {
        return getFirstChildOfType(ASTFormalParameters.class);
    }


    /**
     * Returns the number of formal parameters expected by this declaration.
     */
    default int getArity() {
        return getFormalParameters().getParameterCount();
    }


    /**
     * Returns the body of this method or constructor. Returns null if
     * this is the declaration of an abstract method.
     */
    @Nullable
    default ASTBlock getBody() {
        JavaNode last = getLastChild();
        return last instanceof ASTBlock ? (ASTBlock) last : null;
    }


    /**
     * Returns true if this node's last formal parameter is varargs.
     */
    default boolean isVarargs() {
        JavaNode lastFormal = getFormalParameters().getLastChild();
        return lastFormal instanceof ASTFormalParameter && ((ASTFormalParameter) lastFormal).isVarargs();
    }

}
