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


    @Override
    public boolean isFindBoundary() {
        return true;
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    @Override
    public MethodLikeKind getKind() {
        return MethodLikeKind.LAMBDA;
    }
}
