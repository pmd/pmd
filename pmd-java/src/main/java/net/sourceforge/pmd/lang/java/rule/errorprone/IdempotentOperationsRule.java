/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.AssignmentOp;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;

public class IdempotentOperationsRule extends AbstractJavaRulechainRule {

    public IdempotentOperationsRule() {
        super(ASTAssignmentExpression.class);
    }

    @Override
    public Object visit(ASTAssignmentExpression node, Object data) {
        if (node.getOperator() == AssignmentOp.ASSIGN
            && JavaRuleUtil.isReferenceToSameVar(node.getLeftOperand(), node.getRightOperand())) {
            addViolation(data, node);
        }
        return null;
    }
}
