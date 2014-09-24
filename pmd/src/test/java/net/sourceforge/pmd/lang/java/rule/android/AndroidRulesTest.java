/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.android;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class AndroidRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-android";

    @Override
    public void setUp() {
        addRule(RULESET, "CallSuperFirst");
        addRule(RULESET, "CallSuperLast");
        addRule(RULESET, "DoNotHardCodeSDCard");
    }
}
