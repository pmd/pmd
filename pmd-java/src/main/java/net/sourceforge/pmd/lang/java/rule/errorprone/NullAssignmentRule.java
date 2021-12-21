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
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;

public class NullAssignmentRule extends AbstractJavaRulechainRule {

    public NullAssignmentRule() {
        super(ASTNullLiteral.class);
    }

    @Override
    public Object visit(ASTNullLiteral node, Object data) {
        if (node.getParent() instanceof ASTAssignmentExpression) {
            ASTAssignmentExpression assignment = (ASTAssignmentExpression) node.getParent();
            if (isAssignmentToFinal(assignment)) {
                return data;
            }
            if (assignment.getRightOperand() == node) {
                addViolation(data, node);
            }
        } else if (node.getParent() instanceof ASTConditionalExpression) {
            if (isBadTernary((ASTConditionalExpression) node.getParent(), node)) {
                addViolation(data, node);
            }
        }

        return data;
    }

    private boolean isAssignmentToFinal(ASTAssignmentExpression n) {
        @NonNull
        ASTAssignableExpr leftOperand = n.getLeftOperand();
        if (leftOperand instanceof ASTNamedReferenceExpr) {
            @Nullable
            JVariableSymbol symbol = ((ASTNamedReferenceExpr) leftOperand).getReferencedSym();
            return symbol != null && symbol.isFinal();
        }
        return false;
    }

    private boolean isBadTernary(ASTConditionalExpression ternary, ASTNullLiteral nullLiteral) {
        boolean isInitializer = false;

        ASTVariableDeclarator variableDeclarator = ternary.ancestors(ASTVariableDeclarator.class).first();
        isInitializer = variableDeclarator != null && variableDeclarator.getInitializer() == ternary;

        boolean isThenOrElse = ternary.getThenBranch() == nullLiteral || ternary.getElseBranch() == nullLiteral;

        boolean isAssignment = ternary.getParent() instanceof ASTAssignmentExpression;

        return isThenOrElse
                && isAssignment
                && !isInitializer;
    }
}
