/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

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
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertySource;

/**
 *
 *
 */
public class AssignmentInOperandRule extends AbstractJavaRule {

    private static final PropertyDescriptor<Boolean> ALLOW_IF_DESCRIPTOR =
            booleanProperty("allowIf")
                    .desc("Allow assignment within the conditional expression of an if statement")
                    .defaultValue(false).build();

    private static final PropertyDescriptor<Boolean> ALLOW_FOR_DESCRIPTOR =
            booleanProperty("allowFor")
                    .desc("Allow assignment within the conditional expression of a for statement")
                    .defaultValue(false).build();

    private static final PropertyDescriptor<Boolean> ALLOW_WHILE_DESCRIPTOR =
            booleanProperty("allowWhile")
                    .desc("Allow assignment within the conditional expression of a while statement")
                    .defaultValue(false).build();

    private static final PropertyDescriptor<Boolean> ALLOW_INCREMENT_DECREMENT_DESCRIPTOR =
            booleanProperty("allowIncrementDecrement")
                    .desc("Allow increment or decrement operators within the conditional expression of an if, for, or while statement")
                    .defaultValue(false).build();


    public AssignmentInOperandRule() {
        definePropertyDescriptor(ALLOW_IF_DESCRIPTOR);
        definePropertyDescriptor(ALLOW_FOR_DESCRIPTOR);
        definePropertyDescriptor(ALLOW_WHILE_DESCRIPTOR);
        definePropertyDescriptor(ALLOW_INCREMENT_DECREMENT_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTExpression node, Object data) {
        Node parent = node.getParent();
        if ((parent instanceof ASTIfStatement && !getProperty(ALLOW_IF_DESCRIPTOR)
                || parent instanceof ASTWhileStatement && !getProperty(ALLOW_WHILE_DESCRIPTOR)
                || parent instanceof ASTForStatement && parent.getChild(1) == node
                        && !getProperty(ALLOW_FOR_DESCRIPTOR))
                && (node.hasDescendantOfType(ASTAssignmentOperator.class)
                        || !getProperty(ALLOW_INCREMENT_DECREMENT_DESCRIPTOR)
                                && (node.hasDescendantOfAnyType(ASTPreIncrementExpression.class,
                                                                ASTPreDecrementExpression.class, ASTPostfixExpression.class)))) {

            addViolation(data, node);
            return data;
        }
        return super.visit(node, data);
    }

    public boolean allowsAllAssignments() {
        return getProperty(ALLOW_IF_DESCRIPTOR) && getProperty(ALLOW_FOR_DESCRIPTOR)
                && getProperty(ALLOW_WHILE_DESCRIPTOR) && getProperty(ALLOW_INCREMENT_DECREMENT_DESCRIPTOR);
    }

    /**
     * @see PropertySource#dysfunctionReason()
     */
    @Override
    public String dysfunctionReason() {
        return allowsAllAssignments() ? "All assignment types allowed, no checks performed" : null;
    }
}
