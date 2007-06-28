package test.net.sourceforge.pmd.rules.unusedcode;

import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import java.util.List;
import java.util.ArrayList;

public class UnusedCodeRulesTest extends SimpleAggregatorTst {
    private List<Rule> rules = new ArrayList<Rule>();

    @Before
    public void setUp() {
        rules.add(findRule("unusedcode", "UnusedFormalParameter"));
        rules.add(findRule("unusedcode", "UnusedLocalVariable"));
        rules.add(findRule("unusedcode", "UnusedPrivateField"));
        rules.add(findRule("unusedcode", "UnusedPrivateMethod"));
    }

    @Test
    public void testAll() {
        for (Rule r : rules) {
            runTests(r);
        }
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(UnusedCodeRulesTest.class);
    }
}
