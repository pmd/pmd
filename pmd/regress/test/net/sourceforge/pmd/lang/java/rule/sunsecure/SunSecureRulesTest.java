package test.net.sourceforge.pmd.lang.java.rule.sunsecure;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

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
