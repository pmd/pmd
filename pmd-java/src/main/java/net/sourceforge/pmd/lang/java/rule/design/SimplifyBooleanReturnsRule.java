/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import java.util.Iterator;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.UnaryOp;
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

    /**
     * Checks, whether there is a statement after the given if statement, and if
     * so, whether this is just a return boolean statement.
     *
     * @param ifNode the if statement
     */
    private boolean isFollowedByReturn(ASTIfStatement ifNode) {
        return ifNode.asStream().followingSiblings()
                     .take(1)
                     .filter(it -> it instanceof ASTReturnStatement)
                     .nonEmpty();
    }

    // this method must be symmetric
    private static boolean areComplements(ASTExpression e1, ASTExpression e2) {
        if (isBooleanNegation(e1)) {
            return isEqual(unaryOperand(e1), e2);
        } else if (isBooleanNegation(e2)) {
            return isEqual(e1, unaryOperand(e2));
        } else if (e1 instanceof ASTInfixExpression && e2 instanceof ASTInfixExpression) {
            ASTInfixExpression ifx1 = (ASTInfixExpression) e1;
            ASTInfixExpression ifx2 = (ASTInfixExpression) e2;
            if (ifx1.getOperator().getComplement() != ifx2.getOperator()) {
                return false;
            }
            if (ifx1.getOperator().isEquality()) {
                // NOT(a == b, a != b)
                // NOT(a == b, b != a)
                return isEqual(ifx1.getLeftOperand(), ifx2.getLeftOperand())
                    && isEqual(ifx1.getRightOperand(), ifx2.getRightOperand())
                    || isEqual(ifx2.getLeftOperand(), ifx1.getLeftOperand())
                    && isEqual(ifx2.getRightOperand(), ifx1.getRightOperand());
            }
            // todo we could continue with de Morgan and stuff, and move this into a library
        }
        return false;
    }

    private static boolean isEqual(ASTExpression e1, ASTExpression e2) {
        return tokenEquals(e1, e2);
    }


    private static boolean tokenEquals(JavaNode node, JavaNode that) {
        Iterator<JavaccToken> thisIt = GenericToken.range(node.getFirstToken(), node.getLastToken());
        Iterator<JavaccToken> thatIt = GenericToken.range(that.getFirstToken(), that.getLastToken());
        while (thisIt.hasNext()) {
            if (!thatIt.hasNext()) {
                return false;
            }
            JavaccToken o1 = thisIt.next();
            JavaccToken o2 = thatIt.next();
            if (o1.kind != o2.kind
                || !o2.getImage().equals(o2.getImage())) {
                return false;
            }
        }
        return !thatIt.hasNext();
    }


    private static boolean isBooleanLiteral(ASTExpression e) {
        return e instanceof ASTBooleanLiteral;
    }

    private static boolean isBooleanNegation(ASTExpression e) {
        return e instanceof ASTUnaryExpression && ((ASTUnaryExpression) e).getOperator() == UnaryOp.NEGATION;
    }

    private static ASTExpression unaryOperand(ASTExpression e) {
        return ((ASTUnaryExpression) e).getOperand();
    }

}
