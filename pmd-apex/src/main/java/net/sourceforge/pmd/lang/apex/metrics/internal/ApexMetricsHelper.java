/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.internal;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.apex.ast.ASTBooleanExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTStandardCondition;
import net.sourceforge.pmd.lang.apex.ast.BooleanOperator;

/**
 *
 */
public final class ApexMetricsHelper {

    private ApexMetricsHelper() {
        // utility class
    }

    /**
     * Computes the number of control flow paths through that expression, which is the number of {@code ||} and {@code
     * &&} operators. Used both by Npath and Cyclo.
     *
     * @param expression Boolean expression
     *
     * @return The complexity of the expression
     */
    static int booleanExpressionComplexity(ASTStandardCondition expression) {
        Set<ASTBooleanExpression> subs = new HashSet<>(expression.descendants(ASTBooleanExpression.class).toList());
        int complexity = 0;

        for (ASTBooleanExpression sub : subs) {
            BooleanOperator op = sub.getOp();
            if (op == BooleanOperator.LOGICAL_AND || op == BooleanOperator.LOGICAL_OR) {
                complexity++;
            }
        }

        return complexity;
    }
}
