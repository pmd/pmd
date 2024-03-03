/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;

public class ApexAssertionsShouldIncludeMessageRule extends AbstractApexUnitTestRule {

    private static final String ASSERT = "System.assert";
    private static final String ASSERT_EQUALS = "System.assertEquals";
    private static final String ASSERT_NOT_EQUALS = "System.assertNotEquals";
    private static final String ARE_EQUAL = "Assert.areEqual";
    private static final String ARE_NOT_EQUAL = "Assert.areNotEqual";
    private static final String IS_FALSE = "Assert.isFalse";
    private static final String FAIL = "Assert.fail";
    private static final String IS_INSTANCE_OF_TYPE = "Assert.isInstanceOfType";
    private static final String IS_NOT_INSTANCE_OF_TYPE = "Assert.isNotInstanceOfType";
    private static final String IS_NOT_NULL = "Assert.isNotNull";
    private static final String IS_NULL = "Assert.isNull";
    private static final String IS_TRUE = "Assert.isTrue";

    @Override
    public Object visit(ASTMethodCallExpression node, Object data) {
        String methodName = node.getFullMethodName();

        if (FAIL.equalsIgnoreCase(methodName) && node.getNumChildren() == 1) {
            asCtx(data).addViolationWithMessage(node,
                    "''{0}'' should have 1 parameters.",
                    FAIL);
        } else if ((ASSERT.equalsIgnoreCase(methodName)
                || IS_FALSE.equalsIgnoreCase(methodName)
                || IS_NOT_NULL.equalsIgnoreCase(methodName)
                || IS_NULL.equalsIgnoreCase(methodName)
                || IS_TRUE.equalsIgnoreCase(methodName))
                && node.getNumChildren() == 2) {
            asCtx(data).addViolationWithMessage(node,
                    "''{0}'' should have 2 parameters.",
                    methodName);
        } else if ((ASSERT_EQUALS.equalsIgnoreCase(methodName)
                || ASSERT_NOT_EQUALS.equalsIgnoreCase(methodName)
                || ARE_EQUAL.equalsIgnoreCase(methodName)
                || ARE_NOT_EQUAL.equalsIgnoreCase(methodName)
                || IS_INSTANCE_OF_TYPE.equalsIgnoreCase(methodName)
                || IS_NOT_INSTANCE_OF_TYPE.equalsIgnoreCase(methodName))
                && node.getNumChildren() == 3) {
            asCtx(data).addViolationWithMessage(node,
                    "''{0}'' should have 3 parameters.",
                    methodName);
        }
        return data;
    }
}
