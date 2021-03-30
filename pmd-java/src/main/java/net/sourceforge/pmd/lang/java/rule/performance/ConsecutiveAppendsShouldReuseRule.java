/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

public class ConsecutiveAppendsShouldReuseRule extends AbstractJavaRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTExpressionStatement.class, ASTLocalVariableDeclaration.class);
    }

    @Override
    public Object visit(ASTExpressionStatement node, Object data) {
        Node nextSibling = node.asStream().followingSiblings().first();
        if (nextSibling instanceof ASTExpressionStatement) {
            @Nullable JVariableSymbol variable = getVariableAppended(node);
            if (variable != null) {
                @Nullable JVariableSymbol nextVariable = getVariableAppended((ASTExpressionStatement) nextSibling);
                if (nextVariable != null && nextVariable.equals(variable)) {
                    addViolation(data, node);
                }
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        Node nextSibling = node.asStream().followingSiblings().first();
        if (nextSibling instanceof ASTExpressionStatement) {
            @Nullable JVariableSymbol nextVariable = getVariableAppended((ASTExpressionStatement) nextSibling);
            if (nextVariable != null) {
                ASTVariableDeclaratorId varDecl = nextVariable.tryGetNode();
                if (varDecl != null && node.getVarIds().any(it -> it == varDecl)
                    && isStringBuilderAppend(varDecl.getInitializer())) {
                    addViolation(data, node);
                }
            }
        }
        return data;
    }


    private @Nullable JVariableSymbol getVariableAppended(ASTExpressionStatement node) {
        ASTExpression expr = node.getExpr();
        if (expr instanceof ASTMethodCall) {
            return getAsVarAccess(getAppendChainQualifier(expr));
        } else if (expr instanceof ASTAssignmentExpression) {
            ASTExpression rhs = ((ASTAssignmentExpression) expr).getRightOperand();
            return getAppendChainQualifier(rhs) != null ? getAssignmentLhsAsVar(expr) : null;
        }
        return null;
    }

    private @Nullable ASTExpression getAppendChainQualifier(final ASTExpression base) {
        ASTExpression expr = base;
        while (expr instanceof ASTMethodCall && isStringBuilderAppend(expr)) {
            expr = ((ASTMethodCall) expr).getQualifier();
        }
        return base == expr ? null : expr; // NOPMD
    }

    private @Nullable JVariableSymbol getAssignmentLhsAsVar(@Nullable ASTExpression expr) {
        if (expr instanceof ASTAssignmentExpression) {
            return getAsVarAccess(((ASTAssignmentExpression) expr).getLeftOperand());
        }
        return null;
    }

    private @Nullable JVariableSymbol getAsVarAccess(@Nullable ASTExpression expr) {
        if (expr instanceof ASTNamedReferenceExpr) {
            return ((ASTNamedReferenceExpr) expr).getReferencedSym();
        }
        return null;
    }

    private boolean isStringBuilderAppend(@Nullable ASTExpression e) {
        if (e instanceof ASTMethodCall) {
            ASTMethodCall call = (ASTMethodCall) e;
            return "append".equals(call.getMethodName())
                && isStringBuilderAppend(call.getOverloadSelectionInfo());
        }
        return false;
    }

    private boolean isStringBuilderAppend(OverloadSelectionResult result) {
        if (result.isFailed()) {
            return false;
        }

        JExecutableSymbol symbol = result.getMethodType().getSymbol();
        return TypeTestUtil.isExactlyA(StringBuffer.class, symbol.getEnclosingClass())
            || TypeTestUtil.isExactlyA(StringBuilder.class, symbol.getEnclosingClass());
    }

}
