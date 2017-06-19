/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
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
public abstract class AbstractClassAndOperationMetric extends AbstractClassMetric implements OperationMetric {

    @Override
    public double computeFor(ASTClassOrInterfaceDeclaration node, MetricVersion version, ResultOption option) {
        switch (option) {
        case SUM:
            return sumMetricOverOperations(node, getOperationMetricKey(), version, false);
        case AVERAGE:
            return averageMetricOverOperations(node, getOperationMetricKey(), version, false);
        case HIGHEST:
            return averageMetricOverOperations(node, getOperationMetricKey(), version, false);
        default:
            return sumMetricOverOperations(node, getOperationMetricKey(), version, false);
        }
    }

    /**
     * Return the key of the metric.
     *
     * @return The key of the metric.
     */
    protected abstract OperationMetricKey getOperationMetricKey();
}
