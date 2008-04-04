package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class AssignmentInOperand extends AbstractJavaRule {

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
