/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class GodClassRuleTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-design";

    @Before
    public void setUp() {
        addRule(RULESET, "GodClass");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(GodClassRuleTest.class);
    }
}
