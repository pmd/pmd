/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.performance;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Warn users when code that could impact performance is executing within a
 * looping construct.
 */
public class OperationWithHighCostInLoopRule extends AbstractAvoidNodeInLoopsRule {

    private static final Set<String> SCHEMA_PERFORMANCE_METHODS = CollectionUtil.setOf(
                    "System.Schema.getGlobalDescribe",
                    "Schema.getGlobalDescribe",
                    "System.Schema.describeSObjects",
                    "Schema.describeSObjects")
            .stream().map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toSet());

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(
                // performance consuming methods
                ASTMethodCallExpression.class);
    }

    // Begin general method invocations
    @Override
    public Object visit(ASTMethodCallExpression node, Object data) {
        if (checkHighCostClassMethods(node)) {
            return checkForViolation(node, data);
        } else {
            return data;
        }
    }

    private boolean checkHighCostClassMethods(ASTMethodCallExpression node) {
        return SCHEMA_PERFORMANCE_METHODS.contains(node.getFullMethodName().toLowerCase(Locale.ROOT));
    }
    // End general method invocations
}
