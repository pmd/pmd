/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.rule.basic;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class BasicRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "ecmascript-basic";

    @Before
    public void setUp() {
	addRule(RULESET, "AssignmentInOperand");
	addRule(RULESET, "AvoidTrailingComma");
	addRule(RULESET, "ConsistentReturn");
	addRule(RULESET, "InnaccurateNumericLiteral");
	addRule(RULESET, "ScopeForInVariable");
	addRule(RULESET, "UnreachableCode");
	addRule(RULESET, "EqualComparison");
	addRule(RULESET, "GlobalVariable");
   	addRule(RULESET, "UseBaseWithParseInt");
    }

    public static junit.framework.Test suite() {
	return new junit.framework.JUnit4TestAdapter(BasicRulesTest.class);
    }
}
