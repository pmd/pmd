/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.performance;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class PerformanceRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "apex-performance";

    @Override
    public void setUp() {
        addRule(RULESET, "AvoidNonRestrictiveQueries");
        addRule(RULESET, "AvoidSoqlInLoops");
        addRule(RULESET, "AvoidDmlStatementsInLoops");
    }
}
