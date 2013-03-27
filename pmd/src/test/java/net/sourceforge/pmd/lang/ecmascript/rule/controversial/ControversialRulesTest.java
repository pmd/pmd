/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.rule.controversial;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class ControversialRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "ecmascript-controversial";

    @Before
    public void setUp() {
	    addRule(RULESET, "AvoidWithStatement");
    }

    public static junit.framework.Test suite() {
	    return new junit.framework.JUnit4TestAdapter(ControversialRulesTest.class);
    }
}
