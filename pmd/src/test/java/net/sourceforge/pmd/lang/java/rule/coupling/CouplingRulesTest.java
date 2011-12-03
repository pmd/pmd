package net.sourceforge.pmd.lang.java.rule.coupling;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class CouplingRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-coupling";

    @Before
    public void setUp() {
        addRule(RULESET, "CouplingBetweenObjects");
        addRule(RULESET, "ExcessiveImports");
        addRule(RULESET, "LooseCoupling");
        addRule(RULESET, "LoosePackageCoupling");
        addRule(RULESET, "LawOfDemeter");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(CouplingRulesTest.class);
    }
}
