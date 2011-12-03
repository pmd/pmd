package net.sourceforge.pmd.lang.ecmascript.rule.braces;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class BracesRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "ecmascript-braces";

    @Before
    public void setUp() {
	addRule(RULESET, "ForLoopsMustUseBraces");
	addRule(RULESET, "IfElseStmtsMustUseBraces");
	addRule(RULESET, "IfStmtsMustUseBraces");
	addRule(RULESET, "WhileLoopsMustUseBraces");
    }

    public static junit.framework.Test suite() {
	return new junit.framework.JUnit4TestAdapter(BracesRulesTest.class);
    }
}
