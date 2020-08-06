/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTArrayAllocation;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

public class CompareObjectsWithEqualsRule extends AbstractJavaRule {


    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTInfixExpression.class);
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

        if (left.getTypeMirror().isPrimitive() || right.getTypeMirror().isPrimitive()
            || TypeHelper.isA(left, Enum.class) || TypeHelper.isA(right, Enum.class)
            || TypeHelper.isA(left, Class.class) || TypeHelper.isA(right, Class.class)) {
            return data;
        }
        addViolation(data, node);

        return data;
    }

}
