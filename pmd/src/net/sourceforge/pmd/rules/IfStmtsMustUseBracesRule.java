/*
 * User: tom
 * Date: Sep 5, 2002
 * Time: 2:28:16 PM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.RuleContext;

public class IfStmtsMustUseBracesRule extends BracesRule {

    private int lineNumberOfLastViolation;

    public Object visit(ASTCompilationUnit node, Object data) {
        lineNumberOfLastViolation = -1;
        return super.visit(node,data);
    }

    public Object visit(ASTIfStatement node, Object data) {
        // if..else stmts are covered by other rules
        if (node.jjtGetNumChildren() >= 3) {
            return super.visit(node, data);
        }

        // the first child is a Expression, so skip that and get the first stmt
        SimpleNode child = (SimpleNode)node.jjtGetChild(1);

        if (!hasBlockAsFirstChild(child) && (node.getBeginLine() != lineNumberOfLastViolation)) {
            RuleContext ctx = (RuleContext)data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
            lineNumberOfLastViolation = node.getBeginLine();
        }

        return null;
    }
}
