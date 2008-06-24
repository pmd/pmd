package net.sourceforge.pmd.lang.java.rule.controversial;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTPostfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPreDecrementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPreIncrementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;

public class AssignmentInOperandRule extends AbstractJavaRule {

    private static final PropertyDescriptor ALLOW_IF_DESCRIPTOR = new BooleanProperty("allowIf",
	    "Allows assignment within the conditional expression of an if statement.", false, 1.0f);

    private static final PropertyDescriptor ALLOW_FOR_DESCRIPTOR = new BooleanProperty("allowFor",
	    "Allows assignment within the conditional expression of a for statement.", false, 2.0f);

    private static final PropertyDescriptor ALLOW_WHILE_DESCRIPTOR = new BooleanProperty("allowWhile",
	    "Allows assignment within the conditional expression of a while statement.", false, 3.0f);

    private static final PropertyDescriptor ALLOW_INCREMENT_DECREMENT_DESCRIPTOR = new BooleanProperty(
	    "allowIncrementDecrement",
	    "Allows increment or decrement operators within the conditional expression of an if, for, or while statement.",
	    false, 4.0f);

    public Object visit(ASTExpression node, Object data) {
	Node parent = node.jjtGetParent();
	if (((parent instanceof ASTIfStatement && !getBooleanProperty(ALLOW_IF_DESCRIPTOR))
		|| (parent instanceof ASTWhileStatement && !getBooleanProperty(ALLOW_WHILE_DESCRIPTOR)) || (parent instanceof ASTForStatement
		&& parent.jjtGetChild(1) == node && !getBooleanProperty(ALLOW_FOR_DESCRIPTOR)))
		&& (node.hasDescendantOfType(ASTAssignmentOperator.class) || (!getBooleanProperty(ALLOW_INCREMENT_DECREMENT_DESCRIPTOR) && (node
			.hasDescendantOfType(ASTPreIncrementExpression.class)
			|| node.hasDescendantOfType(ASTPreDecrementExpression.class) || node
			.hasDescendantOfType(ASTPostfixExpression.class))))) {
	    addViolation(data, node);
	    return data;
	}
	return super.visit(node, data);
    }
}
