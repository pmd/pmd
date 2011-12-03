/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.rule.unnecessary;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class UnnecessaryRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "ecmascript-unnecessary";

    @Before
    public void setUp() {
	addRule(RULESET, "UnnecessaryBlock");
	addRule(RULESET, "UnnecessaryParentheses");
    }

    public static junit.framework.Test suite() {
	return new junit.framework.JUnit4TestAdapter(UnnecessaryRulesTest.class);
    }
}
