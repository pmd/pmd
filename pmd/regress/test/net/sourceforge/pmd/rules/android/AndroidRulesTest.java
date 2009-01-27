package test.net.sourceforge.pmd.rules.android;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class AndroidRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("android", "CallSuperFirst");
        addRule("android", "CallSuperLast");
        addRule("android", "ProtectLogD");
        addRule("android", "ProtectLogV");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(AndroidRulesTest.class);
    }
}
