/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.ast.ASTNullLiteral;
import net.sourceforge.pmd.ast.ASTStatementExpression;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.ASTConditionalExpression;

public class NullAssignmentRule extends AbstractRule {

    public Object visit(ASTNullLiteral node, Object data) {
        if (node.jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent() instanceof ASTStatementExpression) {
            Node n = node.jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent();
            if (n.jjtGetNumChildren() > 2 && n.jjtGetChild(1) instanceof ASTAssignmentOperator) {
                RuleContext ctx = (RuleContext) data;
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, node));
            }
        } else if (node.jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent() instanceof ASTConditionalExpression) {
            checkTernary((ASTConditionalExpression)node.jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent(), data, node);
        } else if (node.jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent() instanceof ASTConditionalExpression) {
            checkTernary((ASTConditionalExpression)node.jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent(), data, node);
        }

        return data;
    }

    private void checkTernary(ASTConditionalExpression n, Object data, ASTNullLiteral node) {
        if (n.isTernary()) {
            RuleContext ctx = (RuleContext) data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node));
        }
    }
}
