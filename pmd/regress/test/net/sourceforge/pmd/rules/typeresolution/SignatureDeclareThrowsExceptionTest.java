package test.net.sourceforge.pmd.rules.typeresolution;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class SignatureDeclareThrowsExceptionTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("typeresolution", "SignatureDeclareThrowsException");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(SignatureDeclareThrowsExceptionTest.class);
    }
}
