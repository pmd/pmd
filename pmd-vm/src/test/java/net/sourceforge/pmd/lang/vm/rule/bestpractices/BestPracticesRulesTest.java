/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.rule.bestpractices;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public abstract class BestPracticesRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/vm/bestpractices.xml";

    @Override
    public void setUp() {
        addRule(RULESET, getClass().getSimpleName().replaceFirst("Test$", ""));
    }
}
