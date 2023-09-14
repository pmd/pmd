/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.performance;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.rule.internal.Helper;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

/**
 * Warn users when code that could impact performance is executing within a
 * looping construct.
 */
public class OperationWithHighCostInLoopRule extends AbstractAvoidNodeInLoopsRule {

    private static final String SCHEMA_CLASS_NAME = "Schema";

    private static final String[] SCHEMA_PERFORMANCE_METHODS = new String[] { "getGlobalDescribe", "describeSObjects" };

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(
                // performance consuming methods
                ASTMethodCallExpression.class);
    }

    // Begin general method invocations
    @Override
    public Object visit(ASTMethodCallExpression node, Object data) {
        if (checkHighCostClassMethods(node, SCHEMA_CLASS_NAME, SCHEMA_PERFORMANCE_METHODS)) {
            return checkForViolation(node, data);
        } else {
            return data;
        }
    }

    private boolean checkHighCostClassMethods(ASTMethodCallExpression node, String className, String[] methodNames) {

        for (String method : methodNames) {
            if (Helper.isMethodName(node, className, method)) {
                return true;
            }
        }

        return false;
    }
    // End general method invocations
}
