/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.stat;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.rule.properties.DoubleProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;
import net.sourceforge.pmd.stat.DataPoint;

/**
 * This interface tags a Rule as being a Statistical rule, producing various
 * metrics from data points.
 * 
 * @see DataPoint
 * @see Metric
 * @see StatisticalRuleHelper
 */
public interface StatisticalRule extends Rule {

    DoubleProperty SIGMA_DESCRIPTOR = new DoubleProperty("sigma", "Sigma value", 0d, 100d, null, 1.0f);
    DoubleProperty MINIMUM_DESCRIPTOR = new DoubleProperty("minimum", "Minimum reporting threshold", 0d, 100d, null, 2.0f);
    IntegerProperty TOP_SCORE_DESCRIPTOR = new IntegerProperty("topscore", "Top score value", 1, 100, null, 3.0f);

    void addDataPoint(DataPoint point);
    Object[] getViolationParameters(DataPoint point);
}
