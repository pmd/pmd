package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.ast.ASTExpression;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTWhileStatement;
import net.sourceforge.pmd.ast.Node;

public class AssignmentInOperand extends AbstractRule {

    public Object visit(ASTExpression node, Object data) {
        Node parent = node.jjtGetParent();
        if ((parent instanceof ASTWhileStatement || parent instanceof ASTIfStatement) &&
                node.containsChildOfType(ASTAssignmentOperator.class))
        {
            addViolation(data, node);
            return data;
        }
        return super.visit(node, data);
    }

}
