/*
 * User: tom
 * Date: Jul 19, 2002
 * Time: 11:30:15 PM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTWhileStatement;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTForStatement;

public class ForLoopsMustUseBracesRule  extends BracesRule {

    public Object visit(ASTForStatement node, Object data) {
        RuleContext ctx = (RuleContext)data;
        SimpleNode loopBody = (SimpleNode)node.jjtGetChild(3);

        if (!hasBlockAsFirstChild(loopBody)) {
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
        }
        return super.visit(node,data);
    }

}
