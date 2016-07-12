package net.sourceforge.pmd.lang.apex.rule.apexunit;

import java.util.List;

import com.google.common.collect.Iterables;

import net.sourceforge.pmd.lang.apex.ast.*;

/**
 * Apex unit tests should have System.assert methods in them
 *
 * @author a.subramanian
 */
public class ApexUnitTestClassShouldHaveAsserts extends AbstractApexUnitTestRule {

    private static final String SYSTEM = "System";
    private static final String ASSERT = "assert";
    private static final String ASSERT_EQUALS = "assertEquals";
    private static final String ASSERT_NOT_EQUALS = "assertNotEquals";

    @Override
    public Object visit(ASTMethod node, Object data) {
        if (!isTestMethodOrClass(node)) {
            return data;
        }
        return checkForAssertStatements(node, data);
    }

    private Object checkForAssertStatements(ApexNode<?> node, Object data) {
        final List<ASTBlockStatement> blockStatements = node.findDescendantsOfType(ASTBlockStatement.class);
        final List<ASTStatement> statements = Iterables.getOnlyElement(blockStatements).findDescendantsOfType(ASTStatement.class);
        boolean isAssertFound = false;
        for (final ASTStatement statement : statements) {
            final List<ASTMethodCallExpression> methodCalls = statement.findDescendantsOfType(ASTMethodCallExpression.class);
            for (final ASTMethodCallExpression methodCallExpression : methodCalls) {
                final String methodName = methodCallExpression.getNode().getMethod().getName();
                if (methodCallExpression.getNode().getDefiningType().getApexName().equalsIgnoreCase(SYSTEM)
                    && (methodName.equalsIgnoreCase(ASSERT)
                        || methodName.equalsIgnoreCase(ASSERT_EQUALS)
                        || methodName.equalsIgnoreCase(ASSERT_NOT_EQUALS))) {
                    isAssertFound = true;
                }
            }
        }
        if (!isAssertFound) {
            addViolation(data, node);
        }
        return data;
    }
}
