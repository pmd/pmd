/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertySource;

/**
 *
 */
public class AssignmentInOperandRule extends AbstractJavaRulechainRule {

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
        super(ASTAssignmentExpression.class, ASTUnaryExpression.class);
        definePropertyDescriptor(ALLOW_IF_DESCRIPTOR);
        definePropertyDescriptor(ALLOW_FOR_DESCRIPTOR);
        definePropertyDescriptor(ALLOW_WHILE_DESCRIPTOR);
        definePropertyDescriptor(ALLOW_INCREMENT_DECREMENT_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTAssignmentExpression node, Object data) {
        checkAssignment(node, (RuleContext) data);
        return null;
    }

    @Override
    public Object visit(ASTUnaryExpression node, Object data) {
        if (!getProperty(ALLOW_INCREMENT_DECREMENT_DESCRIPTOR) && !node.getOperator().isPure()) {
            checkAssignment(node, (RuleContext) data);
        }
        return null;
    }

    private void checkAssignment(ASTExpression impureExpr, RuleContext ctx) {
        ASTExpression toplevel = JavaRuleUtil.getTopLevelExpr(impureExpr);
        JavaNode parent = toplevel.getParent();
        if (parent instanceof ASTExpressionStatement) {
            // that's ok
            return;
        }
        if (parent instanceof ASTIfStatement && !getProperty(ALLOW_IF_DESCRIPTOR)
            || parent instanceof ASTWhileStatement && !getProperty(ALLOW_WHILE_DESCRIPTOR)
            || parent instanceof ASTForStatement && ((ASTForStatement) parent).getCondition() == toplevel && !getProperty(ALLOW_FOR_DESCRIPTOR)) {

            addViolation(ctx, impureExpr);
        }
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
