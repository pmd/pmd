/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.AssignmentOp;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

public class IdempotentOperationsRule extends AbstractJavaRulechainRule {

    public IdempotentOperationsRule() {
        super(ASTAssignmentExpression.class);
    }

    @Override
    public Object visit(ASTAssignmentExpression node, Object data) {
        if (node.getOperator() == AssignmentOp.ASSIGN
            && JavaAstUtils.isReferenceToSameVar(node.getLeftOperand(), node.getRightOperand())) {
            addViolation(data, node);
        }
        return null;
    }
}
