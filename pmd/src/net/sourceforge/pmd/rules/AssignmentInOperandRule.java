package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTWhileStatement;

import java.util.ArrayList;
import java.util.List;

public class AssignmentInOperandRule extends AbstractRule implements Rule {

    public Object visit(ASTIfStatement node, Object data) {
        checkForAssignmentInConditionalExpression(node, data);
        return super.visit(node, data);
    }

    public Object visit(ASTWhileStatement node, Object data) {
        checkForAssignmentInConditionalExpression(node, data);
        return super.visit(node, data);
    }

    private void checkForAssignmentInConditionalExpression(SimpleNode node, Object data) {
        List kids = new ArrayList();
        SimpleNode ifExpression = (SimpleNode)node.jjtGetChild(0);
        ifExpression.findChildrenOfType(ASTAssignmentOperator.class, kids);
        if (!kids.isEmpty()) {
            RuleContext ctx = (RuleContext)data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
        }
    }
}
