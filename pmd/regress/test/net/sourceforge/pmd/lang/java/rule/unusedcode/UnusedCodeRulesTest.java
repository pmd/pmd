package test.net.sourceforge.pmd.lang.java.rule.unusedcode;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class UnusedCodeRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("unusedcode", "UnusedFormalParameter");
        addRule("unusedcode", "UnusedLocalVariable");
        addRule("unusedcode", "UnusedPrivateField");
        addRule("unusedcode", "UnusedPrivateMethod");
        addRule("unusedcode", "UnusedModifier");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(UnusedCodeRulesTest.class);
    }
}
