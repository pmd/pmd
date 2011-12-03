package net.sourceforge.pmd.lang.java.rule.unusedcode;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class UnusedCodeRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-unusedcode";

    @Before
    public void setUp() {
        addRule(RULESET, "UnusedFormalParameter");
        addRule(RULESET, "UnusedLocalVariable");
        addRule(RULESET, "UnusedPrivateField");
        addRule(RULESET, "UnusedPrivateMethod");
        addRule(RULESET, "UnusedModifier");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(UnusedCodeRulesTest.class);
    }
}
