/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.ast.ASTExpression;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTStatementExpression;
import net.sourceforge.pmd.ast.SimpleNode;

public class IdempotentOperationsRule extends AbstractRule {

    public Object visit(ASTStatementExpression node, Object data) {
        if (node.jjtGetNumChildren() != 3
                || !(node.jjtGetChild(0) instanceof ASTPrimaryExpression)
                || !(node.jjtGetChild(1) instanceof ASTAssignmentOperator)
                || !(node.jjtGetChild(2) instanceof ASTExpression)
        ) {
            return super.visit(node, data);
        }

        SimpleNode lhs = (SimpleNode)node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
        if (!(lhs instanceof ASTName)) {
            return super.visit(node, data);
        }

        SimpleNode rhs = (SimpleNode)node.jjtGetChild(2).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
        if (!(rhs instanceof ASTName)) {
            return super.visit(node, data);
        }

        if (!lhs.getImage().equals(rhs.getImage())) {
            return super.visit(node, data);
        }

        ((RuleContext) data).getReport().addRuleViolation(createRuleViolation((RuleContext) data, node.getBeginLine(), "Avoid idempotent operations"));
        return data;
    }
}
