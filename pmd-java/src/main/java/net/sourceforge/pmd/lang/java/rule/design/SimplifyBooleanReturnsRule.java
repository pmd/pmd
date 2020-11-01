/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.areComplements;
import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.isBooleanLiteral;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind;

public class SimplifyBooleanReturnsRule extends AbstractJavaRulechainRule {

    public SimplifyBooleanReturnsRule() {
        super(ASTReturnStatement.class);
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        ASTExpression expr = node.getExpr();
        if (expr == null
            || !expr.getTypeMirror().isPrimitive(PrimitiveTypeKind.BOOLEAN)
            || !isThenBranchOfSomeIf(node)) {
            return null;
        }
        return checkIf(node.ancestors(ASTIfStatement.class).firstOrThrow(), data, expr);
    }

    // Only explore the then branch. If we explore the else, then we'll report twice.
    // In the case if .. else, both branches need to be symmetric anyway.
    private boolean isThenBranchOfSomeIf(ASTReturnStatement node) {
        if (node.getParent() instanceof ASTIfStatement) {
            return node.getIndexInParent() == 1;
        }
        if (node.getParent() instanceof ASTBlock
            && ((ASTBlock) node.getParent()).size() == 1
            && node.getParent().getParent() instanceof ASTIfStatement) {
            return node.getParent().getIndexInParent() == 1;
        }
        return false;
    }

    private Object checkIf(ASTIfStatement node, Object data, ASTExpression thenExpr) {
        // that's the case: if..then..return; return;
        ASTExpression elseExpr = getElseExpr(node);
        if (elseExpr == null) {
            return data;
        }

        if (isBooleanLiteral(thenExpr) || isBooleanLiteral(elseExpr)) {
            addViolation(data, node);
        } else if (areComplements(thenExpr, elseExpr)) {
            // if (foo) return !a;
            // else return a;
            addViolation(data, node);
        }
        return data;
    }

    private @Nullable ASTExpression getReturnExpr(JavaNode node) {
        if (node instanceof ASTReturnStatement) {
            return ((ASTReturnStatement) node).getExpr();
        } else if (node instanceof ASTBlock && ((ASTBlock) node).size() == 1) {
            return getReturnExpr(((ASTBlock) node).get(0));
        }
        return null;
    }

    private @Nullable ASTExpression getElseExpr(ASTIfStatement node) {
        return node.hasElse() ? getReturnExpr(node.getElseBranch())
                              : getReturnExpr(node.getNextSibling()); // may be followed immediately by return
    }


}
