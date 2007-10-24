package test.net.sourceforge.pmd.rules.sunsecure;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class SunSecureRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("sunsecure", "MethodReturnsInternalArray");
        addRule("sunsecure", "ArrayIsStoredDirectly");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(SunSecureRulesTest.class);
    }
}
