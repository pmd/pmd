/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.metrics;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.apex.metrics.ApexMetricsHook;
import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * @author Clément Fournier
 */
public class MetricsRulesTest extends SimpleAggregatorTst {


    private static final String RULESET = "apex-metrics";


    @Override
    protected Rule reinitializeRule(Rule rule) {
        ApexMetricsHook.reset();
        return super.reinitializeRule(rule);
    }


    @Override
    public void setUp() {
        addRule(RULESET, "CyclomaticComplexity");
    }
}
