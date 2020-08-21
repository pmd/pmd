/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaParameterList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.LambdaExprMirror;

class LambdaMirrorImpl extends BasePolyMirror<ASTLambdaExpression> implements LambdaExprMirror {


    LambdaMirrorImpl(JavaExprMirrors mirrors, ASTLambdaExpression lambda) {
        super(mirrors, lambda);
    }

    @Override
    public @Nullable List<JTypeMirror> getExplicitParameterTypes() {
        ASTLambdaParameterList parameters = myNode.getParameters();
        if (parameters.size() == 0) {
            return Collections.emptyList();
        }

        List<JTypeMirror> types = parameters.toStream()
                                            .map(ASTLambdaParameter::getTypeNode)
                                            .toList(TypeNode::getTypeMirror);
        return types.size() == 0 ? null : types;
    }

    @Override
    public int getParamCount() {
        return myNode.getParameters().size();
    }

    @Override
    public List<ExprMirror> getResultExpressions() {
        ASTBlock block = myNode.getBlock();
        if (block == null) {
            return Collections.singletonList(factory.getMirror(myNode.getExpression()));
        } else {
            return block.descendants(ASTReturnStatement.class)
                        .map(ASTReturnStatement::getExpr)
                        .toList(factory::getMirror);
        }
    }

    @Override
    public void setFunctionalMethod(JMethodSig methodType) {
        InternalApiBridge.setFunctionalMethod(myNode, methodType);
    }

    @Override
    public boolean isValueCompatible() {
        ASTBlock block = myNode.getBlock();
        if (block == null) {
            return true;
        } else {
            return isLambdaBodyCompatible(block, false);
        }
    }

    @Override
    public boolean isVoidCompatible() {
        ASTBlock block = myNode.getBlock();
        if (block == null) {
            return isExpressionStatement(myNode.getExpression());
        } else {
            return isLambdaBodyCompatible(block, true);
        }
    }


    /**
     * Malformed bodies may be neither (it's a compile error)
     */
    private static boolean isLambdaBodyCompatible(ASTBlock body, boolean voidCompatible) {
        boolean noReturnsWithExpr = body.descendants(ASTReturnStatement.class).none(it -> it.getExpr() != null);
        if (noReturnsWithExpr && !voidCompatible) {
            // normally we should be determining whether the block must complete abruptly on all paths
            return body.descendants(ASTThrowStatement.class).nonEmpty();
        }
        return noReturnsWithExpr == voidCompatible;
    }

    /**
     * Return true if the expression may return void.
     */
    private static boolean isExpressionStatement(ASTExpression body) {
        // statement expression
        return body instanceof ASTMethodCall
            || body instanceof ASTConstructorCall
            || body instanceof ASTAssignmentExpression
            || body instanceof ASTUnaryExpression && !((ASTUnaryExpression) body).getOperator().isPure();

    }
}
