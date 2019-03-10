/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.stat;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.properties.DoubleProperty;
import net.sourceforge.pmd.properties.IntegerProperty;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.stat.Metric;

/**
 * This interface tags a Rule as being a Statistical rule, producing various
 * metrics from data points.
 *
 * @see DataPoint
 * @see Metric
 * @see StatisticalRuleHelper
 * @deprecated Statistical rules can be implemented much more easily with plain
 * rules, and this framework is confusing and under-documented. All interfaces
 * and classes related to this rule will be removed by 7.0.0. See also <a href="https://github.com/pmd/pmd/issues/483">#483</a>.
 */
@Deprecated
public interface StatisticalRule extends Rule {

    // These property descriptors can't map to the new properties framework and
    // should be removed before removing the concrete property classes as part of #1432

    /** @deprecated Not useful, will not be replaced. */
    @Deprecated
    DoubleProperty SIGMA_DESCRIPTOR = new DoubleProperty("sigma", "deprecated! Sigma value", -10000000d, 1000000d, null,
                                                         1.0f);
    // TODO we should have one such property descriptor pro rule, and *not* share it, to allow setting specific defaults
    DoubleProperty MINIMUM_DESCRIPTOR = new DoubleProperty("minimum", "Minimum reporting threshold", -10000000d, 1000000000d, null,
            2.0f);
    /** @deprecated Not useful, will not be replaced. */
    @Deprecated
    IntegerProperty TOP_SCORE_DESCRIPTOR = new IntegerProperty("topscore", "deprecated! Top score value", 1, 100,
                                                               null, 3.0f);

    void addDataPoint(DataPoint point);

    Object[] getViolationParameters(DataPoint point);
}
