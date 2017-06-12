/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codesize;

import net.sourceforge.pmd.lang.java.oom.Metrics.OperationMetricKey;

/**
 * This version calculates the cyclomatic complexity of operations by taking into account the number of paths of the
 * boolean expressions of control flow statements. It uses the metrics framework.
 *
 * @author Donald A. Leckie
 * @version Revised June 12th, 2017 (Clément Fournier)
 * @see net.sourceforge.pmd.lang.java.oom.metrics.CycloMetric
 * @since January 14, 2003
 */
public class CyclomaticComplexityRule extends StdCyclomaticComplexityRule {

    public CyclomaticComplexityRule() {
        super();
        metricKey = OperationMetricKey.CYCLO;
    }

}
