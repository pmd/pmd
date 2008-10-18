package test.net.sourceforge.pmd.lang.java.rule.coupling;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class CouplingRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-coupling";

    @Before
    public void setUp() {
        addRule(RULESET, "CouplingBetweenObjects");
        addRule(RULESET, "ExcessiveImports");
        addRule(RULESET, "LooseCoupling");
        addRule(RULESET, "LoosePackageCoupling");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(CouplingRulesTest.class);
    }
}
