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
        if (typeAllocatedNode.getImage().equals("String") && !(node.jjtGetChild(1) instanceof ASTArrayDimsAndInits)) {

            // if it's String(byte[], int, int), skip it
            ASTArguments args = (ASTArguments)node.jjtGetChild(1);
            if (!skipDueToByteArrayConversion(args)) {
                RuleContext ctx = (RuleContext)data;
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
            }

        }
        return data;
    }

    private boolean skipDueToByteArrayConversion(ASTArguments args) {
        return args.jjtGetNumChildren() > 0 && args.jjtGetChild(0).jjtGetNumChildren() == 3;
    }
}
