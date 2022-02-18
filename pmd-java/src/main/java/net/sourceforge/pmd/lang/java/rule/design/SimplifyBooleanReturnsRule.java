/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.lang.java.rule.internal.JavaAstUtil.areComplements;
import static net.sourceforge.pmd.lang.java.rule.internal.JavaAstUtil.isBooleanLiteral;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaAstUtil;
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
        checkIf(node.ancestors(ASTIfStatement.class).firstOrThrow(), asCtx(data), expr);
        return null;
    }

    // Only explore the then branch. If we explore the else, then we'll report twice.
    // In the case if .. else, both branches need to be symmetric anyway.
    private boolean isThenBranchOfSomeIf(ASTReturnStatement node) {
        if (node.getParent() instanceof ASTIfStatement) {
            return node.getIndexInParent() == 1;
        }
        return node.getParent() instanceof ASTBlock
            && ((ASTBlock) node.getParent()).size() == 1
            && node.getParent().getParent() instanceof ASTIfStatement
            && node.getParent().getIndexInParent() == 1;
    }

    private void checkIf(ASTIfStatement node, RuleContext data, ASTExpression thenExpr) {
        // that's the case: if..then..return; return;
        ASTExpression elseExpr = getElseExpr(node);
        if (elseExpr == null) {
            return;
        }

        if (isBooleanLiteral(thenExpr) || isBooleanLiteral(elseExpr)) {
            data.addViolation(node);
        } else if (areComplements(thenExpr, elseExpr)) {
            // if (foo) return !a;
            // else return a;
            data.addViolation(node);
        }
    }

    /**
     * Whether refactoring an if-then-else to use shortcut operators
     * would require adding parentheses to respect operator precedence.
     * <pre>{@code
     * if (cond) true   else expr      ->  cond || expr
     * if (cond) false  else expr      -> !cond && expr
     * if (cond) expr   else true      -> !cond || expr
     * if (cond) expr   else false     ->  cond && expr
     * if (!cond) false else expr      -> cond && expr
     * if (!cond) expr  else true     ->  cond || expr
     * }</pre>
     * Note that both the `expr` and the `condition` may require parentheses
     * (if the cond has to be negated). Note that the `expr` and `condition` may
     * themselves be literals, or a negated expr.
     */
    private Result needsToBeReported(ASTExpression condition,
                                     ASTExpression thenExpr,
                                     ASTExpression elseExpr) {

        if (JavaAstUtil.isBooleanLiteral(condition)
        && ) {

        }


        boolean isAndOp = JavaAstUtil.isBooleanLiteral(thenExpr, true)
            || JavaAstUtil.isBooleanLiteral(elseExpr, true);

    }
    enum Result {
        LITERAL_CONDITION
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
