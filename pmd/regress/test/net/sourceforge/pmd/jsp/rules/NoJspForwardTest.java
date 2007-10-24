package test.net.sourceforge.pmd.jsp.rules;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Test the "NoJspForward" rule.
 * 
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class NoJspForwardTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("jsp", "NoJspForward");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(NoJspForwardTest.class);
    }
}
