/*
 * User: tom
 * Date: Sep 12, 2002
 * Time: 1:52:02 PM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTArrayDimsAndInits;

public class StringInstantiationRule extends AbstractRule {

    public Object visit(ASTAllocationExpression node, Object data) {
        SimpleNode typeAllocatedNode = (SimpleNode)node.jjtGetChild(0);
        if (typeAllocatedNode.getImage().equals("String") && !(node.jjtGetChild(1) instanceof ASTArrayDimsAndInits)) {
            RuleContext ctx = (RuleContext)data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
        }
        return data;
    }
}
