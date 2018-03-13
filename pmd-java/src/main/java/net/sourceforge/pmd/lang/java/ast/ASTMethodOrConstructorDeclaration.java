/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

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

    //
    // Enable this with PMD 7.0.0
    //
    ///**
    // * Returns the formal parameters node of this method or constructor.
    // */
    //ASTFormalParameters getFormalParameters();

}
