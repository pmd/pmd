/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Rule tests for the performance category
 */
public class PerformanceRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/java/performance.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "AvoidUsingShortType");
        addRule(RULESET, "BigIntegerInstantiation");
        addRule(RULESET, "BooleanInstantiation");
    }

}
