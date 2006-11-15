package test.net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class SimplifyBooleanAssertionTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = findRule("junit", "SimplifyBooleanAssertion");
    }

    public void testAll() {
        runTests(rule);
    }
}
