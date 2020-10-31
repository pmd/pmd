/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.areComplements;
import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.isBooleanLiteral;
import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.isBooleanNegation;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
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
        return visit(node.ancestors(ASTIfStatement.class).firstOrThrow(), data);
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

    private @Nullable ASTReturnStatement asReturnStatement(ASTStatement node) {
        if (node instanceof ASTReturnStatement) {
            return (ASTReturnStatement) node;
        } else if (node instanceof ASTBlock && ((ASTBlock) node).size() == 1) {
            return asReturnStatement(((ASTBlock) node).get(0));
        }
        return null;
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        // that's the case: if..then..return; return;
        if (!node.hasElse()) {
            if (isFollowedByReturn(node)) {
                addViolation(data, node);
            }
            return data;
        }

        ASTReturnStatement returnStatement1 = asReturnStatement(node.getElseBranch());
        ASTReturnStatement returnStatement2 = asReturnStatement(node.getThenBranch());

        if (returnStatement1 != null && returnStatement2 != null) {
            ASTExpression e1 = returnStatement1.getExpr();
            ASTExpression e2 = returnStatement2.getExpr();

            if (isBooleanLiteral(e1) && isBooleanLiteral(e2)) {
                addViolation(data, node);
            } else if (isBooleanNegation(e1) ^ isBooleanNegation(e2)) {
                // We get the nodes under the '!' operator
                // If they are the same => error
                if (areComplements(e1, e2)) {
                    // if (foo) return !a;
                    // else return a;
                    addViolation(data, node);
                }
            }
        }
        return data;
    }

    private boolean isFollowedByReturn(ASTIfStatement ifNode) {
        return ifNode.asStream().followingSiblings().first() instanceof ASTReturnStatement;
    }


}
