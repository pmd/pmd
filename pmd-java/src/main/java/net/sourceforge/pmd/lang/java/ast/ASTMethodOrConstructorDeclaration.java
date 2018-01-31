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
public abstract class ASTMethodOrConstructorDeclaration extends MethodLike implements SignedNode<ASTMethodOrConstructorDeclaration> {
    private JavaOperationSignature signature;


    public ASTMethodOrConstructorDeclaration(int i) {
        super(i);
    }


    public ASTMethodOrConstructorDeclaration(JavaParser parser, int i) {
        super(parser, i);
    }


    @Override
    public JavaOperationSignature getSignature() {
        if (signature == null) {
            signature = JavaOperationSignature.buildFor(this);
        }

        return signature;
    }
}
