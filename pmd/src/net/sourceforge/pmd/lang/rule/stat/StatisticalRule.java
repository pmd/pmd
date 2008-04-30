package net.sourceforge.pmd.lang.rule.stat;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.stat.Metric;

/**
 * This interface tags a Rule as being a Statistical rule, producing various
 * metrics from data points.
 * 
 * @see DataPoint
 * @see Metric
 * @see StatisticalRuleHelper
 */
public interface StatisticalRule extends Rule {
    void addDataPoint(DataPoint point);
    Object[] getViolationParameters(DataPoint point);
}
