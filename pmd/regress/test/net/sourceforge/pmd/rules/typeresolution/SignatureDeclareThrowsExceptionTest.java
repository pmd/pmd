package test.net.sourceforge.pmd.rules.typeresolution;

import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class SignatureDeclareThrowsExceptionTest extends SimpleAggregatorTst {

    private Rule rule;

    @Before
    public void setUp() {
        rule = findRule("typeresolution", "SignatureDeclareThrowsException");
    }

    @Test
    public void testAll() {
        runTests(rule);
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(SignatureDeclareThrowsExceptionTest.class);
    }
}
