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
 * @see MethodLike
 * @since 5.8.1
 */
public interface ASTMethodOrConstructorDeclaration extends MethodLike, SignedNode<ASTMethodOrConstructorDeclaration> {
    @Override
    JavaOperationSignature getSignature();
}
