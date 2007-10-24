package test.net.sourceforge.pmd.jsp.rules;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class IframeMissingSrcAttributeTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("jsp", "IframeMissingSrcAttribute");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(IframeMissingSrcAttributeTest.class);
    }
}
