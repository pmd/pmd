package test.net.sourceforge.pmd.rules.braces;

import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import java.util.List;
import java.util.ArrayList;

public class BracesRulesTest extends SimpleAggregatorTst {
    private List<Rule> rules = new ArrayList<Rule>();

    @Before
    public void setUp() {
        rules.add(findRule("braces", "ForLoopsMustUseBraces"));
        rules.add(findRule("braces", "IfElseStmtsMustUseBraces"));
        rules.add(findRule("braces", "IfStmtsMustUseBraces"));
        rules.add(findRule("braces", "WhileLoopsMustUseBraces"));
    }

    @Test
    public void testAll() {
        for (Rule r : rules) {
            runTests(r);
        }
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(BracesRulesTest.class);
    }
}
