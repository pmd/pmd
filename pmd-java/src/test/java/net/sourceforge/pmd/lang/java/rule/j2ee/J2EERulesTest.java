/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.j2ee;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class J2EERulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-j2ee";

    @Override
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
}
