package test.net.sourceforge.pmd.rules.finalizers;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class FinalizersRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("finalizers", "AvoidCallingFinalize");
        addRule("finalizers", "EmptyFinalizer");
        addRule("finalizers", "FinalizeDoesNotCallSuperFinalize");
        addRule("finalizers", "FinalizeOnlyCallsSuperFinalize");
        addRule("finalizers", "FinalizeOverloaded");
        addRule("finalizers", "FinalizeShouldBeProtected");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(FinalizersRulesTest.class);
    }
}
