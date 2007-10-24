package test.net.sourceforge.pmd.rules.coupling;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class CouplingRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("coupling", "CouplingBetweenObjects");
        addRule("coupling", "ExcessiveImports");
        addRule("coupling", "LooseCoupling");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(CouplingRulesTest.class);
    }
}
