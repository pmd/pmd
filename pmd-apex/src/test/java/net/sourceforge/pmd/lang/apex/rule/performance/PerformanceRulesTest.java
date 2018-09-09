/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.performance;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public abstract class PerformanceRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/apex/performance.xml";

    @Override
    public void setUp() {
        addRule(RULESET, getClass().getSimpleName().replaceFirst("Test$", ""));
    }
}
