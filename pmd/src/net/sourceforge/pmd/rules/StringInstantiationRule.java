/*
 * User: tom
 * Date: Sep 12, 2002
 * Time: 1:52:02 PM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.*;

public class StringInstantiationRule extends AbstractRule {

    public Object visit(ASTAllocationExpression node, Object data) {
        SimpleNode typeAllocatedNode = (SimpleNode)node.jjtGetChild(0);
        if (typeAllocatedNode.getImage().equals("String") // get new String()
                && !(node.jjtGetChild(1) instanceof ASTArrayDimsAndInits)) { // but not new String[]
            if (((ASTArguments)node.jjtGetChild(1)).getArgumentCount()<=1) {
                RuleContext ctx = (RuleContext)data;
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
            }
        }
        return data;
    }
}
