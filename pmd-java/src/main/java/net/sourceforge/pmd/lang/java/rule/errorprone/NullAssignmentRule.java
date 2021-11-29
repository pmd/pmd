/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;

public class NullAssignmentRule extends AbstractJavaRulechainRule {

    public NullAssignmentRule() {
        super(ASTNullLiteral.class);
    }

    @Override
    public Object visit(ASTNullLiteral node, Object data) {
        if (node.getParent() instanceof ASTAssignmentExpression) {
            ASTAssignmentExpression assignment = (ASTAssignmentExpression) node.getParent();
            if (isAssignmentToFinalField(assignment)) {
                return data;
            }
            if (assignment.getRightOperand() == node) {
                addViolation(data, node);
            }
        } else if (node.getParent() instanceof ASTConditionalExpression) {
            if (isBadTernary((ASTConditionalExpression) node.getParent())) {
                addViolation(data, node);
            }
        }

        return data;
    }

    private boolean isAssignmentToFinalField(ASTAssignmentExpression n) {
        @NonNull
        ASTAssignableExpr leftOperand = n.getLeftOperand();
        if (JavaRuleUtil.isRefToFieldOfThisInstance(leftOperand)) {
            @Nullable
            JVariableSymbol symbol = ((ASTNamedReferenceExpr) leftOperand).getReferencedSym();
            return symbol != null && symbol.isFinal();
        }
        return false;
    }

    private boolean isBadTernary(ASTConditionalExpression ternary) {
        boolean isInitializer = false;

        ASTVariableDeclarator variableDeclarator = ternary.ancestors(ASTVariableDeclarator.class).first();
        isInitializer = variableDeclarator != null && variableDeclarator.getInitializer() == ternary;

        return !(ternary.getCondition() instanceof ASTInfixExpression)
                && !isInitializer
                && !(ternary.getParent() instanceof ASTReturnStatement)
                && !(ternary.getParent() instanceof ASTLambdaExpression);
    }
}
