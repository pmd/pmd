/*
 * User: tom
 * Date: Jun 27, 2002
 * Time: 11:40:07 AM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.ASTStatement;
import net.sourceforge.pmd.ast.ASTWhileStatement;

public class EmptyWhileStmtRule extends AbstractRule implements Rule {

    /**
     * We're looking for anything other than
     * WhileStmt
     *  Expression
     *  Statement
     *   Block
     *    BlockStmt
     */
    public Object visit(ASTWhileStatement node, Object data) {
        RuleContext ctx = (RuleContext) data;
        ASTStatement stmt = (ASTStatement) node.jjtGetChild(1);

        // can this happen?  an Statement without a child?
        if (stmt.jjtGetNumChildren() == 0) {
            return super.visit(node, data);
        }

        // an Statement whose child is not a Block... this might be caught be another braces type rule
        if (!(stmt.jjtGetChild(0) instanceof ASTBlock)) {
            return super.visit(node, data);
        }

        ASTBlock block = (ASTBlock) stmt.jjtGetChild(0);
        // this block must have 1 child, and that child must be a BlockStatement
        if (block.jjtGetNumChildren() == 0 || (!(block.jjtGetChild(0) instanceof ASTBlockStatement))) {
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
        }
        return super.visit(node, data);
    }
}
