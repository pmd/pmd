package net.sourceforge.pmd.lang.java.rule.strictexception;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class StrictExceptionRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-strictexception";

    @Before
    public void setUp() {
        addRule(RULESET, "AvoidCatchingGenericException");
        addRule(RULESET, "AvoidCatchingNPE");
        addRule(RULESET, "AvoidCatchingThrowable");
        addRule(RULESET, "AvoidLosingExceptionInformation");
        addRule(RULESET, "AvoidRethrowingException");
        addRule(RULESET, "AvoidThrowingNewInstanceOfSameException");
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
