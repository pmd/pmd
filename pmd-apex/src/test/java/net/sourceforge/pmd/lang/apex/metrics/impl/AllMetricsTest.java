/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.impl;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Executes the metrics testing rules.
 *
 * @author Clément Fournier
 */
public class AllMetricsTest extends SimpleAggregatorTst {


    private static final String RULESET = "rulesets/apex/metrics_test.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "CycloTest");
        addRule(RULESET, "WmcTest");
        addRule(RULESET, "CognitiveComplexityTest");
    }

}
