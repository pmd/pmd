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
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypingContext;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.LambdaExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ast.JavaExprMirrors.MirrorMaker;
import net.sourceforge.pmd.util.AssertionUtil;

class LambdaMirrorImpl extends BaseFunctionalMirror<ASTLambdaExpression> implements LambdaExprMirror {

    private final List<JVariableSymbol> formalSymbols;

    LambdaMirrorImpl(JavaExprMirrors mirrors, ASTLambdaExpression lambda, @Nullable ExprMirror parent, MirrorMaker subexprMaker) {
        super(mirrors, lambda, parent, subexprMaker);

        if (isExplicitlyTyped()) {
            formalSymbols = Collections.emptyList();
        } else {
            // we'll have one tentative binding per formal param
            formalSymbols = myNode.getParameters().toStream().toList(p -> p.getVarId().getSymbol());

            // initialize the typing context
            TypingContext parentCtx = parent == null ? TypingContext.DEFAULT : parent.getTypingContext();
            List<JTypeMirror> unknownFormals = Collections.nCopies(formalSymbols.size(), null);
            setTypingContext(parentCtx.andThenZip(formalSymbols, unknownFormals));
        }
    }

    @Override
    public boolean isEquivalentToUnderlyingAst() {
        JTypeMirror inferredType = getInferredType();
        JMethodSig inferredMethod = getInferredMethod();
        AssertionUtil.validateState(inferredType != null && inferredMethod != null,
                                    "overload resolution is not complete");

        ASTLambdaParameterList astFormals = myNode.getParameters();
        List<JTypeMirror> thisFormals = inferredMethod.getFormalParameters();
        for (int i = 0; i < thisFormals.size(); i++) {
            if (!thisFormals.get(i).equals(astFormals.get(i).getTypeMirror())) {
                return false;
            }
        }
        // The intuition is that if all lambda parameters and enclosing
        // parameters in the mirror mean the same as in the node,
        // then all expressions occurring in the lambda must mean the
        // same too.
        return true;
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
        return types.isEmpty() ? null : types;
    }

    @Override
    public int getParamCount() {
        return myNode.getParameters().size();
    }

    @Override
    public List<ExprMirror> getResultExpressions() {
        ASTBlock block = myNode.getBlock();
        if (block == null) {
            return Collections.singletonList(createSubexpression(myNode.getExpression()));
        } else {
            return block.descendants(ASTReturnStatement.class)
                        .map(ASTReturnStatement::getExpr)
                        .toList(this::createSubexpression);
        }
    }

    @Override
    public void updateTypingContext(JMethodSig groundFun) {
        if (!isExplicitlyTyped()) {
            // update bindings
            setTypingContext(getTypingContext().andThenZip(formalSymbols, groundFun.getFormalParameters()));
        }
    }

    @Override
    public boolean isValueCompatible() {
        ASTBlock block = myNode.getBlock();
        return block == null || isLambdaBodyCompatible(block, false);
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
