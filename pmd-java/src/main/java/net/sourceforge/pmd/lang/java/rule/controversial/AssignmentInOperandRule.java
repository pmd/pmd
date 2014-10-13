/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.controversial;

import net.sourceforge.pmd.PropertySource;
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

/**
 *
 *
 */
public class AssignmentInOperandRule extends AbstractJavaRule {

    private static final BooleanProperty ALLOW_IF_DESCRIPTOR = new BooleanProperty("allowIf",
	    "Allow assignment within the conditional expression of an if statement", false, 1.0f);

    private static final BooleanProperty ALLOW_FOR_DESCRIPTOR = new BooleanProperty("allowFor",
	    "Allow assignment within the conditional expression of a for statement", false, 2.0f);

    private static final BooleanProperty ALLOW_WHILE_DESCRIPTOR = new BooleanProperty("allowWhile",
	    "Allow assignment within the conditional expression of a while statement", false, 3.0f);

    private static final BooleanProperty ALLOW_INCREMENT_DECREMENT_DESCRIPTOR = new BooleanProperty(
	    "allowIncrementDecrement",
	    "Allow increment or decrement operators within the conditional expression of an if, for, or while statement",
	    false, 4.0f);

    public AssignmentInOperandRule() {
	definePropertyDescriptor(ALLOW_IF_DESCRIPTOR);
	definePropertyDescriptor(ALLOW_FOR_DESCRIPTOR);
	definePropertyDescriptor(ALLOW_WHILE_DESCRIPTOR);
	definePropertyDescriptor(ALLOW_INCREMENT_DECREMENT_DESCRIPTOR);
    }

    @Override
	public Object visit(ASTExpression node, Object data) {
	Node parent = node.jjtGetParent();
	if (((parent instanceof ASTIfStatement && !getProperty(ALLOW_IF_DESCRIPTOR))
		|| (parent instanceof ASTWhileStatement && !getProperty(ALLOW_WHILE_DESCRIPTOR)) ||
		(parent instanceof ASTForStatement && parent.jjtGetChild(1) == node && !getProperty(ALLOW_FOR_DESCRIPTOR))) &&
		(node.hasDescendantOfType(ASTAssignmentOperator.class) ||
		(!getProperty(ALLOW_INCREMENT_DECREMENT_DESCRIPTOR) &&
		(node.hasDecendantOfAnyType(ASTPreIncrementExpression.class, ASTPreDecrementExpression.class, ASTPostfixExpression.class))))) {

	    addViolation(data, node);
	    return data;
	}
	return super.visit(node, data);
    }


	public boolean allowsAllAssignments() {
		return
			getProperty(ALLOW_IF_DESCRIPTOR) &&
			getProperty(ALLOW_FOR_DESCRIPTOR) &&
			getProperty(ALLOW_WHILE_DESCRIPTOR) &&
			getProperty(ALLOW_INCREMENT_DECREMENT_DESCRIPTOR);
	}

	/**
	 * @see PropertySource#dysfunctionReason()
	 */
	@Override
	public String dysfunctionReason() {
		return allowsAllAssignments() ? "All assignment types allowed, no checks performed" : null;
	}
}
