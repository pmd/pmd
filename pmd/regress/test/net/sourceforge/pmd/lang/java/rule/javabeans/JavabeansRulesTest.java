package test.net.sourceforge.pmd.lang.java.rule.javabeans;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class JavabeansRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-javabeans";

    @Before
    public void setUp() {
        addRule(RULESET, "BeanMembersShouldSerialize");
        addRule(RULESET, "MissingSerialVersionUID");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(JavabeansRulesTest.class);
    }
}
