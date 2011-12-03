/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.jsp.rule.basicjsf;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class BasicJsfRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "jsp-basic-jsf";

    @Before
    public void setUp() {
	addRule(RULESET, "DontNestJsfInJstlIteration");
    }

    public static junit.framework.Test suite() {
	return new junit.framework.JUnit4TestAdapter(BasicJsfRulesTest.class);
    }
}
