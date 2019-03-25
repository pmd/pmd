/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import java.util.Locale;

import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexUnitTestRule;

public class ApexAssertionsShouldIncludeMessageRule extends AbstractApexUnitTestRule {

    private static final String ASSERT = "System.assert";
    private static final String ASSERT_EQUALS = "System.assertEquals";
    private static final String ASSERT_NOT_EQUALS = "System.assertNotEquals";

    @Override
    public Object visit(ASTMethodCallExpression node, Object data) {
        String methodName = node.getFullMethodName().toLowerCase(Locale.ROOT);
        if (!ASSERT.equalsIgnoreCase(methodName)
                && !ASSERT_EQUALS.equalsIgnoreCase(methodName)
                && !ASSERT_NOT_EQUALS.equalsIgnoreCase(methodName)) {
            return data;
        }
        if (ASSERT.equalsIgnoreCase(methodName)
                && node.jjtGetNumChildren() == 2) {
            addViolationWithMessage(data, node,
                    "''{0}'' should have 2 parameters.",
                    new Object[] { ASSERT });
        } else if ((ASSERT_EQUALS.equalsIgnoreCase(methodName)
                || ASSERT_NOT_EQUALS.equalsIgnoreCase(methodName))
                && node.jjtGetNumChildren() == 3) {
            addViolationWithMessage(data, node,
                    "''{0}'' should have 3 parameters.",
                    new Object[] { methodName });
        }
        return data;
    }
}
