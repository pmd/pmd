package test.net.sourceforge.pmd.rules.finalizers;

import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import java.util.List;
import java.util.ArrayList;

public class FinalizersRulesTest extends SimpleAggregatorTst {
    private List<Rule> rules = new ArrayList<Rule>();

    @Before
    public void setUp() {
        rules.add(findRule("finalizers", "AvoidCallingFinalize"));
        rules.add(findRule("finalizers", "EmptyFinalizer"));
        rules.add(findRule("finalizers", "FinalizeDoesNotCallSuperFinalize"));
        rules.add(findRule("finalizers", "FinalizeOnlyCallsSuperFinalize"));
        rules.add(findRule("finalizers", "FinalizeOverloaded"));
        rules.add(findRule("finalizers", "FinalizeShouldBeProtected"));
    }

    @Test
    public void testAll() {
        for (Rule r : rules) {
            runTests(r);
        }
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(FinalizersRulesTest.class);
    }
}
