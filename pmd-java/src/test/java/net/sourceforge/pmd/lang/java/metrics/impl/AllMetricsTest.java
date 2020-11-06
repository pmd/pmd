/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Executes the metrics testing rules.
 *
 * @author Clément Fournier
 */
public class AllMetricsTest extends SimpleAggregatorTst {


    private static final String RULESET = "rulesets/java/metrics_test.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "CycloTest");
        addRule(RULESET, "NcssTest");
        addRule(RULESET, "WmcTest");
        addRule(RULESET, "LocTest");
        addRule(RULESET, "NPathTest");
        addRule(RULESET, "NopaTest");
        addRule(RULESET, "NoamTest");
        addRule(RULESET, "WocTest");
        addRule(RULESET, "TccTest");
        addRule(RULESET, "AtfdTest");
        addRule(RULESET, "CfoTest");
    }

}
