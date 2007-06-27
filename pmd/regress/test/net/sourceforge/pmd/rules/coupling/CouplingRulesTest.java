package test.net.sourceforge.pmd.rules.coupling;

import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import java.util.List;
import java.util.ArrayList;

public class CouplingRulesTest extends SimpleAggregatorTst {
    private List<Rule> rules = new ArrayList<Rule>();

    @Before
    public void setUp() {
        rules.add(findRule("coupling", "CouplingBetweenObjects"));
        rules.add(findRule("coupling", "ExcessiveImports"));
        rules.add(findRule("coupling", "LooseCoupling"));
    }

    @Test
    public void testAll() {
        for (Rule r : rules) {
            runTests(r);
        }
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(CouplingRulesTest.class);
    }
}
