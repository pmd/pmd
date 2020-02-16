/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.apex.ast.ASTBooleanExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTStandardCondition;
import net.sourceforge.pmd.lang.apex.metrics.impl.visitors.StandardCycloVisitor;
import net.sourceforge.pmd.lang.metrics.MetricOptions;

import apex.jorje.data.ast.BooleanOp;

/**
 * See the doc for the Java metric.
 *
 * @author Clément Fournier
 */
public class CycloMetric extends AbstractApexOperationMetric {


    @Override
    public double computeFor(ASTMethod node, MetricOptions options) {
        return ((MutableInt) node.jjtAccept(new StandardCycloVisitor(), new MutableInt(1))).doubleValue();
    }


    /**
     * Computes the number of control flow paths through that expression, which is the number of {@code ||} and {@code
     * &&} operators. Used both by Npath and Cyclo.
     *
     * @param expression Boolean expression
     *
     * @return The complexity of the expression
     */
    public static int booleanExpressionComplexity(ASTStandardCondition expression) {
        Set<ASTBooleanExpression> subs = new HashSet<>(expression.findDescendantsOfType(ASTBooleanExpression.class));
        int complexity = 0;

        for (ASTBooleanExpression sub : subs) {
            BooleanOp op = sub.getOperator();
            if (op != null && (op == BooleanOp.AND || op == BooleanOp.OR)) {
                complexity++;
            }
        }

        return complexity;
    }
}
