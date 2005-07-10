/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.ast.ASTConditionalExpression;
import net.sourceforge.pmd.ast.ASTNullLiteral;
import net.sourceforge.pmd.ast.ASTStatementExpression;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.ASTEqualityExpression;

// Would this be simplified by using DFA somehow?
public class NullAssignmentRule extends AbstractRule {

    public Object visit(ASTNullLiteral node, Object data) {
        if (lookUp(node) instanceof ASTStatementExpression) {
            Node n = lookUp(node);
            if (n.jjtGetNumChildren() > 2 && n.jjtGetChild(1) instanceof ASTAssignmentOperator) {
                RuleContext ctx = (RuleContext) data;
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, node));
            }
        } else if (lookUp2(node) instanceof ASTConditionalExpression) {
            checkTernary((ASTConditionalExpression)lookUp2(node), data, node);
        } else if (lookUp(node) instanceof ASTConditionalExpression) {
            checkTernary((ASTConditionalExpression)lookUp(node), data, node);
        }

        return data;
    }

    private Node lookUp2(ASTNullLiteral node) {
        return node.jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent();
    }

    private Node lookUp(ASTNullLiteral node) {
        return lookUp2(node).jjtGetParent();
    }

    private void checkTernary(ASTConditionalExpression n, Object data, ASTNullLiteral node) {
        if (n.isTernary() && !(n.jjtGetChild(0) instanceof ASTEqualityExpression)) {
            RuleContext ctx = (RuleContext) data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node));
        }
    }
}
