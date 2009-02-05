package test.net.sourceforge.pmd.rules.strictexception;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class StrictExceptionRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("strictexception", "AvoidCatchingNPE");
        addRule("strictexception", "AvoidCatchingThrowable");
        addRule("strictexception", "AvoidRethrowingException");
        addRule("strictexception", "AvoidThrowingNewInstanceOfSameException");
        addRule("strictexception", "AvoidThrowingNullPointerException");
        addRule("strictexception", "AvoidThrowingRawExceptionTypes");
        addRule("strictexception", "DoNotExtendJavaLangError");
        addRule("strictexception", "ExceptionAsFlowControl");
        addRule("strictexception", "SignatureDeclareThrowsException");
        addRule("strictexception", "DoNotThrowExceptionInFinally");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(StrictExceptionRulesTest.class);
    }
}
