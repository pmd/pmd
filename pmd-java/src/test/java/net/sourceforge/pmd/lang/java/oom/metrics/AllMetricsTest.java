/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import org.junit.After;
import org.junit.AfterClass;

import net.sourceforge.pmd.lang.java.oom.Metrics;
import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * @author Clément Fournier
 */
public class AllMetricsTest extends SimpleAggregatorTst {


    private static final String RULESET = "rulesets/java/metrics_test.xml";


    @Override
    public Rule reset() {
        System.err.println("resettt");
        Metrics.reset();
    }


    @Override
    public void setUp() {
        addRule(RULESET, "CycloTest");
        addRule(RULESET, "NcssTest");
        addRule(RULESET, "WmcTest");
        addRule(RULESET, "LocTest");
    }

}
