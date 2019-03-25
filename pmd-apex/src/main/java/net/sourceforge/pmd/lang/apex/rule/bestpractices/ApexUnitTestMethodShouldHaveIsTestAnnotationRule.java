/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import java.util.List;
import java.util.Locale;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexUnitTestRule;

public class ApexUnitTestMethodShouldHaveIsTestAnnotationRule extends AbstractApexUnitTestRule {
    private static final String ASSERT = "system.assert";
    private static final String ASSERT_EQUALS = "system.assertequals";
    private static final String ASSERT_NOT_EQUALS = "system.assertnotequals";

    @Override
    public Object visit(final ASTMethod node, final Object data) {
        // test methods should have @isTest annotation.
        if (isTestMethodOrClass(node)) {
            return data;
        }
        return checkForAssertStatements(node, data);
    }

    private Object checkForAssertStatements(final ASTMethod testMethod, final Object data) {
        List<ASTMethodCallExpression> methodCallList = testMethod.findDescendantsOfType(ASTMethodCallExpression.class);
        String assertMethodName;
        for (ASTMethodCallExpression assertMethodCall : methodCallList) {
            assertMethodName = assertMethodCall.getFullMethodName().toLowerCase(Locale.ROOT);
            if (ASSERT.equalsIgnoreCase(assertMethodName)
                    || ASSERT_EQUALS.equalsIgnoreCase(assertMethodName)
                    || ASSERT_NOT_EQUALS.equalsIgnoreCase(assertMethodName)) {
                addViolationWithMessage(data, testMethod,
                        "''{0}'' method should have @IsTest annotation.",
                        new Object[] { testMethod.getImage() });
                return data;
            }
        }
        return data;
    }
}
