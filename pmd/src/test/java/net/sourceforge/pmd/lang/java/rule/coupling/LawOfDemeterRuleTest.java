/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.coupling;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class LawOfDemeterRuleTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-coupling";

    @Before
    public void setUp() {
        addRule(RULESET, "LawOfDemeter");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(LawOfDemeterRuleTest.class);
    }
}
