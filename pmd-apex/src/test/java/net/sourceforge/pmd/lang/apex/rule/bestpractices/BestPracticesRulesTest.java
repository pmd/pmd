/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * @author a.subramanian
 */
public class BestPracticesRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/apex/bestpractices.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "ApexUnitTestClassShouldHaveAsserts");
        addRule(RULESET, "ApexUnitTestShouldNotUseSeeAllDataTrue");
        addRule(RULESET, "AvoidGlobalModifier");
        addRule(RULESET, "AvoidLogicInTrigger");
    }
}
