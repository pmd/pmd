/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTArrayAllocation;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTThisExpression;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

public class CompareObjectsWithEqualsRule extends AbstractJavaRulechainRule {

    public CompareObjectsWithEqualsRule() {
        super(ASTInfixExpression.class);
    }

    /** Indicate whether this node is allocating a new object. */
    private boolean isAllocation(ASTExpression n) {
        return n instanceof ASTConstructorCall || n instanceof ASTArrayAllocation;
    }

    @Override
    public Object visit(ASTInfixExpression node, Object data) {
        if (!node.getOperator().isEquality()) {
            return data;
        }
        ASTExpression left = node.getLeftOperand();
        ASTExpression right = node.getRightOperand();

        // If either side is allocating a new object, there's no way an
        // equals expression is correct
        if (isAllocation(left) || isAllocation(right)) {
            addViolation(data, node);
            return data;
        }

        if (!isAllowedIdentityComparison(node)) {
            addViolation(data, node);
        }

        return data;
    }

    private boolean isIgnoredType(ASTExpression left) {
        return left.getTypeMirror().isPrimitive()
            || TypeTestUtil.isA(Enum.class, left)
            || TypeTestUtil.isA(Class.class, left);
    }

    private boolean isAllowedIdentityComparison(ASTInfixExpression infix) {
        ASTBodyDeclaration enclosing = infix.ancestors(ASTBodyDeclaration.class).first();

        ASTExpression left = infix.getLeftOperand();
        ASTExpression right = infix.getRightOperand();
        if (isEqualsMethod(enclosing) && (isThisExpr(left) || isThisExpr(right))) {
            // this == _ is allowed in equals methods
            return true;
        } else {
            return isIgnoredType(left) || isIgnoredType(right);
        }
    }

    private static boolean isThisExpr(@NonNull ASTExpression expr) {
        return expr instanceof ASTThisExpression;
    }

    private static boolean isEqualsMethod(ASTBodyDeclaration first) {
        if (first instanceof ASTMethodDeclaration) {
            ASTMethodDeclaration m = (ASTMethodDeclaration) first;
            return "equals".equals(m.getName())
                && m.getArity() == 1
                && TypeTestUtil.isExactlyA(Object.class, m.getFormalParameters().get(0));
        }
        return false;
    }

}
