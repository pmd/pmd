package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import java.util.ArrayList;
import java.util.List;

public class ScratchpadRulesTest extends SimpleAggregatorTst {
    private List<Rule> rules = new ArrayList<Rule>();

    @Before
    public void setUp() {
        //rules.add(findRule("scratchpad", "rulename"));
    }

    @Test
    public void testAll() {
        for (Rule r : rules) {
            runTests(r);
        }
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ScratchpadRulesTest.class);
    }

}
