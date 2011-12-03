package net.sourceforge.pmd.lang.java.rule.finalizers;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class FinalizersRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-finalizers";

    @Before
    public void setUp() {
        addRule(RULESET, "AvoidCallingFinalize");
        addRule(RULESET, "EmptyFinalizer");
        addRule(RULESET, "FinalizeDoesNotCallSuperFinalize");
        addRule(RULESET, "FinalizeOnlyCallsSuperFinalize");
        addRule(RULESET, "FinalizeOverloaded");
        addRule(RULESET, "FinalizeShouldBeProtected");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(FinalizersRulesTest.class);
    }
}
