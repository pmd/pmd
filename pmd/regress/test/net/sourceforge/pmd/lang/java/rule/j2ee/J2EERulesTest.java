package test.net.sourceforge.pmd.lang.java.rule.j2ee;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class J2EERulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-j2ee";

    @Before
    public void setUp() {
    	addRule(RULESET, "DoNotCallSystemExit");
    	addRule(RULESET, "DoNotUseThreads");
        addRule(RULESET, "LocalHomeNamingConvention");
        addRule(RULESET, "LocalInterfaceSessionNamingConvention");
        addRule(RULESET, "MDBAndSessionBeanNamingConvention");
        addRule(RULESET, "RemoteInterfaceNamingConvention");
        addRule(RULESET, "RemoteSessionInterfaceNamingConvention");
        addRule(RULESET, "StaticEJBFieldShouldBeFinal");
        addRule(RULESET, "UseProperClassLoader");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(J2EERulesTest.class);
    }
}
