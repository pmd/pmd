package test.net.sourceforge.pmd.rules.basic;

import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class UselessOperationOnImmutableTest extends SimpleAggregatorTst {
    private Rule rule;

    @Before
    public void setUp() {
        rule = findRule("basic", "UselessOperationOnImmutable");
    }

    @Test
    public void testAll() {
        runTests(rule);
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(UselessOperationOnImmutableTest.class);
    }
}
