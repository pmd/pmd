/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTLambdaExpression extends AbstractMethodLikeNode {

    @InternalApi
    @Deprecated
    public ASTLambdaExpression(int id) {
        super(id);
    }


    @InternalApi
    @Deprecated
    public ASTLambdaExpression(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public boolean isFindBoundary() {
        return true;
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public MethodLikeKind getKind() {
        return MethodLikeKind.LAMBDA;
    }
}
