package net.sourceforge.pmd.lang.apex.rule.apexunit;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * @author a.subramanian
 */
public class ApexUnitRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "apex-apexunit";

    @Override
    public void setUp() {
//        addRule(RULESET, "ApexUnitTestClassShouldHaveAsserts");
        addRule(RULESET, "ApexUnitTestShouldNotUseSeeAllDataTrue");
    }
}
