/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Rule tests for the best practices category
 */
public abstract class BestPracticesRulesTest extends SimpleAggregatorTst {

    protected static final String RULESET = "category/java/bestpractices.xml";

    // missing rule: AvoidUsingHardCodedURL

    @Override
    public void setUp() {
        addRule(RULESET, getClass().getSimpleName().replaceFirst("Test$", ""));
    }
}
