/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
/* Generated By:JJTree: Do not edit this line. ASTLambdaExpression.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */

package net.sourceforge.pmd.lang.java.ast;

public class ASTLambdaExpression extends AbstractMethodLikeNode {
    public ASTLambdaExpression(int id) {
        super(id);
    }


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
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
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
