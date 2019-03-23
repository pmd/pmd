/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexUnitTestRule;

public class ApexUnitTestMethodShouldHaveIsTestAnnotationRule extends AbstractApexUnitTestRule {
    private static final Set<String> ASSERT_METHODS = new HashSet<>();
    private static final String ASSERT = "system.assert";
    private static final String ASSERT_EQUALS = "system.assertequals";
    private static final String ASSERT_NOT_EQUALS = "system.assertnotequals";

    static {
        ASSERT_METHODS.add(ASSERT);
        ASSERT_METHODS.add(ASSERT_EQUALS);
        ASSERT_METHODS.add(ASSERT_NOT_EQUALS);
    }

    public ApexUnitTestMethodShouldHaveIsTestAnnotationRule() {
        addRuleChainVisit(ASTMethod.class);
    }

    @Override
    public Object visit(final ASTMethod node, final Object data) {
        // test methods should have @isTest annotation.
        if (isTestMethodOrClass(node)) {
            return data;
        }
        return checkForIsTestAnnotation(node, data);
    }

    private Object checkForIsTestAnnotation(final ApexNode<?> node, final Object data) {
        ASTMethod testMethod = (ASTMethod) node;
        if (testMethod == null) {
            return data;
        }
        List<ASTMethodCallExpression> methodCallList = testMethod.findDescendantsOfType(ASTMethodCallExpression.class);
        for (ASTMethodCallExpression assertMethodCall : methodCallList) {
            if (ASSERT_METHODS.contains(assertMethodCall.getFullMethodName().toLowerCase(Locale.ROOT))) {
                addViolationWithMessage(data, node,
                        "''{0}'' method should have @IsTest annotation.",
                        new Object[] { testMethod.getImage() });
                return data;
            }
        }
        return data;
    }
}
