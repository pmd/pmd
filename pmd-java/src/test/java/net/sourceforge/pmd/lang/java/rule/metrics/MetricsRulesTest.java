/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.metrics;

import net.sourceforge.pmd.lang.java.oom.MetricsForceHook;
import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * @author Clément Fournier
 */

public class MetricsRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-metrics";


    static {
        MetricsForceHook.setForce(true); // TODO:cf get rid of that when you can
    }


    @Override
    public void setUp() {
        addRule(RULESET, "CyclomaticComplexity");
        addRule(RULESET, "NcssCount");
    }
}
