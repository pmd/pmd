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


    /** Accept the visitor. **/
    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public MethodLikeKind getKind() {
        return MethodLikeKind.LAMBDA;
    }
}
/*
 * JavaCC - OriginalChecksum=e706de031abe9a22c368b7cb52802f1b (do not edit this
 * line)
 */
