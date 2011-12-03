package test.net.sourceforge.pmd.lang.java.rule.typeresolution;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class SignatureDeclareThrowsExceptionTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-typeresolution";

    @Before
    public void setUp() {
        addRule(RULESET, "SignatureDeclareThrowsException");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(SignatureDeclareThrowsExceptionTest.class);
    }
}
