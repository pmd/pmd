/*
 * User: tom
 * Date: Aug 14, 2002
 * Time: 10:43:59 AM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTReturnStatement;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTBooleanLiteral;

public class SimplifyBooleanReturnsRule extends AbstractRule {

    public Object visit(ASTIfStatement node, Object data) {
        // only deal with if..then..else stmts
        if (node.jjtGetNumChildren() != 3) {
            return super.visit(node, data);
        }

        // first case:
        // If
        //  Expr
        //  Statement
        //   ReturnStatement
        //  Statement
        //   ReturnStatement
        // i.e.,
        // if (foo)
        //  return true;
        // else
        //  return false;
        if (node.jjtGetChild(1).jjtGetChild(0) instanceof ASTReturnStatement
         && node.jjtGetChild(2).jjtGetChild(0) instanceof ASTReturnStatement
         && terminatesInBooleanLiteral((SimpleNode)node.jjtGetChild(1).jjtGetChild(0))
         && terminatesInBooleanLiteral((SimpleNode)node.jjtGetChild(2).jjtGetChild(0))) {
            RuleContext ctx = (RuleContext)data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
        } else if (false) {
            // second case
            // If
            // Expr
            // Statement
            //  Block
            //   BlockStatement
            //    Statement
            //     ReturnStatement
            // Statement
            //  Block
            //   BlockStatement
            //    Statement
            //     ReturnStatement
            // i.e.,
            // if (foo) {
            //  return true;
            // } else {
            //  return false;
            // }
        }

        return super.visit(node, data);
    }

    private boolean terminatesInBooleanLiteral(SimpleNode node) {
        return eachNodeHasOneChild(node) && (getLastChild(node) instanceof ASTBooleanLiteral);
    }

    private boolean eachNodeHasOneChild(SimpleNode node) {
        if (node.jjtGetNumChildren() > 1) {
            return false;
        }
        if (node.jjtGetNumChildren() == 0) {
            return true;
        }
        return eachNodeHasOneChild((SimpleNode)node.jjtGetChild(0));
    }

    private SimpleNode getLastChild(SimpleNode node) {
        if (node.jjtGetNumChildren() == 0) {
            return node;
        } else {
            return getLastChild((SimpleNode)node.jjtGetChild(0));
        }
    }
}
