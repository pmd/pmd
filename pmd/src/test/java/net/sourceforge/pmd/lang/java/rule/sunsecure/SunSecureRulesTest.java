package net.sourceforge.pmd.lang.java.rule.sunsecure;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class SunSecureRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-sunsecure";

    @Before
    public void setUp() {
        addRule(RULESET, "MethodReturnsInternalArray");
        addRule(RULESET, "ArrayIsStoredDirectly");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(SunSecureRulesTest.class);
    }
}
