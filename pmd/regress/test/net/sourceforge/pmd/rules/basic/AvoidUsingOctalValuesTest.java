package test.net.sourceforge.pmd.rules.basic;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class AvoidUsingOctalValuesTest extends SimpleAggregatorTst {

    private Rule rule;

    @Before
    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("basic", "AvoidUsingOctalValues");
    }

    @Test
    public void testAll() {
        runTests(rule);
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(AvoidUsingOctalValuesTest.class);
    }
}
