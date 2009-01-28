package test.net.sourceforge.pmd.lang.java.rule.android;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class AndroidRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-android";

    @Before
    public void setUp() {
        addRule(RULESET, "CallSuperFirst");
        addRule(RULESET, "CallSuperLast");
        addRule(RULESET, "ProtectLogD");
        addRule(RULESET, "ProtectLogV");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(AndroidRulesTest.class);
    }
}
