package test.net.sourceforge.pmd.rules.clone;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class CloneRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("clone", "CloneMethodMustImplementCloneable");
        addRule("clone", "CloneThrowsCloneNotSupportedException");
        addRule("clone", "ProperCloneImplementation");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(CloneRulesTest.class);
    }
}
