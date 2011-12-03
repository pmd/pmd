/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class UncommentedEmptyMethodRuleTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-design";

    @Before
    public void setUp() {
        addRule(RULESET, "UncommentedEmptyMethod");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(
                UncommentedEmptyMethodRuleTest.class);
    }
}
