/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSignature;


@Deprecated
@InternalApi
public abstract class AbstractMethodOrConstructorDeclaration extends AbstractMethodLikeNode implements ASTMethodOrConstructorDeclaration {

    private JavaOperationSignature signature;


    AbstractMethodOrConstructorDeclaration(int i) {
        super(i);
    }


    AbstractMethodOrConstructorDeclaration(JavaParser parser, int i) {
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
