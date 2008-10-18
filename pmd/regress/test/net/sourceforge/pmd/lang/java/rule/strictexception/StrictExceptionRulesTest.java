package test.net.sourceforge.pmd.lang.java.rule.strictexception;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class StrictExceptionRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-strictexception";

    @Before
    public void setUp() {
        addRule(RULESET, "AvoidCatchingNPE");
        addRule(RULESET, "AvoidCatchingThrowable");
        addRule(RULESET, "AvoidRethrowingException");
        addRule(RULESET, "AvoidThrowingNullPointerException");
        addRule(RULESET, "AvoidThrowingRawExceptionTypes");
        addRule(RULESET, "DoNotExtendJavaLangError");
        addRule(RULESET, "ExceptionAsFlowControl");
        addRule(RULESET, "SignatureDeclareThrowsException");
        addRule(RULESET, "DoNotThrowExceptionInFinally");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(StrictExceptionRulesTest.class);
    }
}
