/*
 * User: tom
 * Date: Jun 26, 2002
 * Time: 10:21:05 AM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.SimpleNode;

public class IfElseStmtsMustUseBracesRule extends BracesRule {

    private int lineNumberOfLastViolation;

    public Object visit(ASTCompilationUnit node, Object data) {
        lineNumberOfLastViolation = -1;
        return super.visit(node, data);
    }

    public Object visit(ASTIfStatement node, Object data) {
        // filter out if stmts without an else
        if (node.jjtGetNumChildren() < 3) {
            return super.visit(node, data);
        }

        // the first child is a Expression, so skip that and get the first 2 stmts
        SimpleNode firstStmt = (SimpleNode) node.jjtGetChild(1);
        SimpleNode secondStmt = (SimpleNode) node.jjtGetChild(2);

        if (!hasBlockAsFirstChild(firstStmt) && !hasBlockAsFirstChild(secondStmt) && (node.getBeginLine() != this.lineNumberOfLastViolation)) {
            RuleContext ctx = (RuleContext) data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
            lineNumberOfLastViolation = node.getBeginLine();
        }

        return super.visit(node, data);
    }
}
