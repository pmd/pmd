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
import net.sourceforge.pmd.lang.metrics.MetricVersion;

import apex.jorje.semantic.ast.expression.BooleanExpressionType;

/**
 * See the doc for the Java metric.
 *
 * @author Clément Fournier
 */
public class CycloMetric extends AbstractApexOperationMetric {


    @Override
    public double computeFor(ASTMethod node, MetricVersion version) {
        return ((MutableInt) node.jjtAccept(new StandardCycloVisitor(), new MutableInt(1))).doubleValue();
    }


    public static int booleanExpressionComplexity(ASTStandardCondition expression) {
        Set<ASTBooleanExpression> subs = new HashSet<>(expression.findDescendantsOfType(ASTBooleanExpression.class));
        int complexity = 0;

        for (ASTBooleanExpression sub : subs) {
            BooleanExpressionType type = sub.getNode().getBooleanExpressionType();
            if (type != null && (type == BooleanExpressionType.OR || type == BooleanExpressionType.AND)) {
                complexity++;
            }
        }

        return complexity;
    }
}
