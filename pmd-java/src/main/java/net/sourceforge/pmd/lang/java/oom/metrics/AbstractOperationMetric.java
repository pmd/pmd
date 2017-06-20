/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.oom.AbstractMetric;
import net.sourceforge.pmd.lang.java.oom.api.MetricVersion;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetric;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.ResultOption;

/**
 * Provides common logic for the treatment of ResultOptions by metrics implementing both ClassMetric and
 * OperationMetric.
 *
 * @author Clément Fournier
 */
public abstract class AbstractOperationMetric extends AbstractMetric implements OperationMetric {

    /**
     * Actually computes the value of a metric for an AST node.
     *
     * <p>This implementation makes the behaviour of ResultOptions consistent for all metrics that are both class and
     * operation metrics. It makes use of {@link #getOperationMetricKey()} to compute the value of the operation metric
     * on the operations of the class, so it's necessary to implement that.
     *
     * @param node    The node.
     * @param version A possibly empty list of options.
     * @param option  The result option to use.
     *
     * @return The value of the metric depending on the ResultOption.
     */
    @Override
    public final double computeFor(ASTClassOrInterfaceDeclaration node, MetricVersion version, ResultOption option) {
        switch (option) {
        case SUM:
            return sumMetricOverOperations(node, getOperationMetricKey(), version, false);
        case AVERAGE:
            return averageMetricOverOperations(node, getOperationMetricKey(), version, false);
        case HIGHEST:
            return highestMetricOverOperations(node, getOperationMetricKey(), version, false);
        default:
            throw new RuntimeException("The result option was null inside the metric class!"); // impossible
        }
    }

    // TODO:cf remove the need for that method, that's useless boilerplate
    /**
     * Return the operation metric key of the metric.
     *
     * @return The key of the metric.
     */
    protected abstract OperationMetricKey getOperationMetricKey();

}
