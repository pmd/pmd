package test.net.sourceforge.pmd.rules.j2ee;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class J2EERulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
    	addRule("j2ee", "DoNotCallSystemExit");
    	addRule("j2ee", "DoNotUseThreads");
        addRule("j2ee", "LocalHomeNamingConvention");
        addRule("j2ee", "LocalInterfaceSessionNamingConvention");
        addRule("j2ee", "MDBAndSessionBeanNamingConvention");
        addRule("j2ee", "RemoteInterfaceNamingConvention");
        addRule("j2ee", "RemoteSessionInterfaceNamingConvention");
        addRule("j2ee", "StaticEJBFieldShouldBeFinal");
        addRule("j2ee", "UseProperClassLoader");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(J2EERulesTest.class);
    }
}
