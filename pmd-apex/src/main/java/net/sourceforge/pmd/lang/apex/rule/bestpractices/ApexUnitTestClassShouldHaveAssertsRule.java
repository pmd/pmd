/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTStatement;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexUnitTestRule;

/**
 * Apex unit tests should have System.assert methods in them
 *
 * @author a.subramanian
 */
public class ApexUnitTestClassShouldHaveAssertsRule extends AbstractApexUnitTestRule {

    private static final Set<String> ASSERT_METHODS = new HashSet<>();

    static {
        ASSERT_METHODS.add("system.assert");
        ASSERT_METHODS.add("system.assertequals");
        ASSERT_METHODS.add("system.assertnotequals");
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        if (!isTestMethodOrClass(node)) {
            return data;
        }

        return checkForAssertStatements(node, data);
    }

    private Object checkForAssertStatements(ApexNode<?> node, Object data) {
        final List<ASTBlockStatement> blockStatements = node.findDescendantsOfType(ASTBlockStatement.class);
        final List<ASTStatement> statements = new ArrayList<>();
        final List<ASTMethodCallExpression> methodCalls = new ArrayList<>();
        for (ASTBlockStatement blockStatement : blockStatements) {
            statements.addAll(blockStatement.findDescendantsOfType(ASTStatement.class));
            methodCalls.addAll(blockStatement.findDescendantsOfType(ASTMethodCallExpression.class));
        }
        boolean isAssertFound = false;

        for (final ASTMethodCallExpression methodCallExpression : methodCalls) {
            if (ASSERT_METHODS.contains(methodCallExpression.getFullMethodName().toLowerCase(Locale.ROOT))) {
                isAssertFound = true;
                break;
            }
        }

        if (!isAssertFound) {
            addViolation(data, node);
        }

        return data;
    }
}
