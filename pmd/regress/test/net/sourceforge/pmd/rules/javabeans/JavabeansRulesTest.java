package test.net.sourceforge.pmd.rules.javabeans;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class JavabeansRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("javabeans", "BeanMembersShouldSerialize");
        addRule("javabeans", "MissingSerialVersionUID");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(JavabeansRulesTest.class);
    }
}
