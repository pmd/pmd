/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.unnecessary;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Rule tests for the runnecessary ruleset
 */
public class UnnecessaryRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-unnecessary";

    @Override
    public void setUp() {
        addRule(RULESET, "UnnecessaryConversionTemporary");
        addRule(RULESET, "UnnecessaryReturn");
        addRule(RULESET, "UnnecessaryFinalModifier");
        addRule(RULESET, "UnusedNullCheckInEquals");
        addRule(RULESET, "UselessOverridingMethod");
        addRule(RULESET, "UselessOperationOnImmutable");
        addRule(RULESET, "UselessParentheses");
    }
}
