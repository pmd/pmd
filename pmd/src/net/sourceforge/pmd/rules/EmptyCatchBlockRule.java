/*
 * User: tom
 * Date: Jun 14, 2002
 * Time: 12:13:55 PM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTCatch;
import net.sourceforge.pmd.ast.ASTTryStatement;

import java.util.Iterator;

public class EmptyCatchBlockRule extends AbstractRule implements Rule {

    public Object visit(ASTTryStatement node, Object data){
        RuleContext ctx = (RuleContext)data;
        // this skips try..finally constructs since they don't have catch blocks
        if (!node.hasCatch()) {
            return super.visit(node, data);
        }

        for (Iterator i = node.getCatchBlocks().iterator(); i.hasNext();) {
            ASTBlock block = ((ASTCatch)i.next()).getBlock();
            if (block.jjtGetNumChildren() == 0) {
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, block.getBeginLine()));
            }
        }
        return super.visit(node, data);
    }

}
