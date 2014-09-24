/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.sunsecure;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class SunSecureRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-sunsecure";

    @Override
    public void setUp() {
        addRule(RULESET, "MethodReturnsInternalArray");
        addRule(RULESET, "ArrayIsStoredDirectly");
    }
}
