/*
 * User: tom
 * Date: Jun 26, 2002
 * Time: 10:21:05 AM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;

public class IfElseStmtsMustUseBracesRule extends AbstractRule implements Rule {

    private int lineNumberOfLastViolation;

    public String getDescription() {
        return "Avoid using IF...ELSE statements without curly braces";
    }

    /**
     * If..else stmt structure seems to be like this:
     * IfStmt
     *  Expression
     *  Stmt
     *   Block
     *  Stmt
     *   Block
     * if (foo == null) {
     *  return bar;
     * } else {
     *  return buz;
     * }
     *
     * Sometimes people get lazy and leave out the Blocks, like this:
     * if (foo== null)
     *  return bar;
     * else
     *  return buz;
     *
     * The following usage is OK though:
     * IfStmt
     *  Expression
     *  Stmt
     * i.e.:
     * if (foo == null)
     *  return bar;
     *
     */
    public Object visit(ASTIfStatement node, Object data) {
        // filter out if stmts without an else
        if (node.jjtGetNumChildren() < 3) {
            return super.visit(node, data);
        }

        // the first child is a Expression, so skip that and get the first 2 stmts
        SimpleNode firstStmt = (SimpleNode)node.jjtGetChild(1);
        SimpleNode secondStmt = (SimpleNode)node.jjtGetChild(2);

        if  (!hasBlockAsFirstChild(firstStmt) && !hasBlockAsFirstChild(secondStmt)) {
            if (node.getBeginLine() != this.lineNumberOfLastViolation) {
                Report rpt = (Report)data;
                rpt.addRuleViolation(new RuleViolation(this, node.getBeginLine()));
                this.lineNumberOfLastViolation = node.getBeginLine();
            }
        }
        return super.visit(node,data);
    }

    private boolean hasBlockAsFirstChild(SimpleNode node) {
        return (node.jjtGetNumChildren() != 0 && (node.jjtGetChild(0) instanceof ASTBlock));
    }
}
