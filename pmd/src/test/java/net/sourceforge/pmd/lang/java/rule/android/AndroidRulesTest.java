package net.sourceforge.pmd.lang.java.rule.android;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class AndroidRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-android";

    @Before
    public void setUp() {
        addRule(RULESET, "CallSuperFirst");
        addRule(RULESET, "CallSuperLast");
        addRule(RULESET, "DoNotHardCodeSDCard");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(AndroidRulesTest.class);
    }
}
