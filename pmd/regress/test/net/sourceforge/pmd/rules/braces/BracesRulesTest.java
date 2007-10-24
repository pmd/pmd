package test.net.sourceforge.pmd.rules.braces;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class BracesRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("braces", "ForLoopsMustUseBraces");
        addRule("braces", "IfElseStmtsMustUseBraces");
        addRule("braces", "IfStmtsMustUseBraces");
        addRule("braces", "WhileLoopsMustUseBraces");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(BracesRulesTest.class);
    }
}
