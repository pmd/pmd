/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.oom.AbstractClassMetric;
import net.sourceforge.pmd.lang.java.oom.OperationMetricKey;
import net.sourceforge.pmd.lang.java.oom.interfaces.ClassMetric;
import net.sourceforge.pmd.lang.java.oom.interfaces.MetricVersion;

/**
 * Weighed Method Count. It is the sum of the statical complexity of all operations of a class. We use
 * {@link CycloMetric} to quantify the complexity of a metric. [1]
 *
 * <p>[1] Lanza. Object-Oriented Metrics in Practice.
 *
 * @author Clément Fournier
 * @since June 2017
 */
public class WmcMetric extends AbstractClassMetric implements ClassMetric {

    @Override
    public double computeFor(ASTClassOrInterfaceDeclaration node, MetricVersion version) {
        return sumMetricOverOperations(node, getTopLevelPackageStats(), OperationMetricKey.CYCLO, false);
    }
}
