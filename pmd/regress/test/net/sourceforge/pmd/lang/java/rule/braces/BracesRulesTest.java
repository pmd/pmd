package test.net.sourceforge.pmd.lang.java.rule.braces;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class BracesRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-braces";

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
