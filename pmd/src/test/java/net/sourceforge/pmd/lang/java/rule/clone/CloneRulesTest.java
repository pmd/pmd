package net.sourceforge.pmd.lang.java.rule.clone;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class CloneRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-clone";

    @Before
    public void setUp() {
        addRule(RULESET, "CloneMethodMustImplementCloneable");
        addRule(RULESET, "CloneThrowsCloneNotSupportedException");
        addRule(RULESET, "ProperCloneImplementation");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(CloneRulesTest.class);
    }
}
