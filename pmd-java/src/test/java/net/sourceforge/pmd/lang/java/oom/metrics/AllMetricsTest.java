/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * @author Clément Fournier
 */
public class AllMetricsTest extends SimpleAggregatorTst {

    private static final String RULESET = "rulesets/java/metrics_test.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "CyclomaticComplexity");
    }



}
