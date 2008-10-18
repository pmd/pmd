/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.lang.java.rule.unnecessary;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class UnnecessaryRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-unnecessary";

    @Before
    public void setUp() {
        addRule(RULESET, "UnnecessaryConversionTemporary");
        addRule(RULESET, "UnnecessaryReturn");
        addRule(RULESET, "UnnecessaryFinalModifier");
        addRule(RULESET, "UselessOverridingMethod");
        addRule(RULESET, "UselessOperationOnImmutable");
        addRule(RULESET, "UselessParentheses");	
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(UnnecessaryRulesTest.class);
    }
}
