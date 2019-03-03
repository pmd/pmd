/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexUnitTestRule;

/**
 * Apex unit tests should have System.assert methods in them
 *
 * @author sudhansu
 */
public class ApexUnitTestAssertStatementRule extends AbstractApexUnitTestRule {

    private static final Set<String> ASSERT_METHODS = new HashSet<>();
    private static final String ASSERT = "system.assert";
    private static final String ASSERT_EQUALS = "system.assertequals";
    private static final String ASSERT_NOT_EQUALS = "system.assertnotequals";

    static {
        ASSERT_METHODS.add(ASSERT);
        ASSERT_METHODS.add(ASSERT_EQUALS);
        ASSERT_METHODS.add(ASSERT_NOT_EQUALS);
    }

    public ApexUnitTestAssertStatementRule() {
        addRuleChainVisit(ASTMethodCallExpression.class);
    }

    @Override
    public Object visit(ASTMethodCallExpression node, Object data) {
        if (!ASSERT_METHODS.contains(node.getFullMethodName().toLowerCase(Locale.ROOT))) {
            return data;
        }

        return checkForAssertStatement(node, data);
    }

    protected Object checkForAssertStatement(ApexNode<?> node, Object data) {
        // if System.assert has 1 argument, throw error
        // if System.assertEquals/System.assertNotEquals has only 2 arguments, throw error
        ASTMethodCallExpression assertMethodCall = (ASTMethodCallExpression) node;
        if (assertMethodCall == null) {
            return data;
        }
        if (ASSERT.equalsIgnoreCase(assertMethodCall.getFullMethodName())
                && assertMethodCall.jjtGetNumChildren() == 2) {
            addViolationWithMessage(data, node,
                    "''{0}'' should have 2 parameters.",
                    new Object[] { ASSERT });
        } else if ((ASSERT_EQUALS.equalsIgnoreCase(assertMethodCall.getFullMethodName())
                || ASSERT_NOT_EQUALS.equalsIgnoreCase(assertMethodCall.getFullMethodName()))
                && assertMethodCall.jjtGetNumChildren() == 3) {
            Object obj = ASSERT_EQUALS.equalsIgnoreCase(assertMethodCall.getFullMethodName())
                    ? ASSERT_EQUALS : ASSERT_NOT_EQUALS;
            addViolationWithMessage(data, node,
                    "''{0}'' should have 3 parameters.",
                    new Object[] { obj });
        }
        return data;
    }
}
