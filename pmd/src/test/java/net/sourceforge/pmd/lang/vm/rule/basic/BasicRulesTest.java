package net.sourceforge.pmd.lang.vm.rule.basic;

import org.junit.Before;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class BasicRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "vm-basic";

    @Before
    public void setUp() {
        addRule(RULESET, "AvoidDeeplyNestedIfStmts");
        addRule(RULESET, "CollapsibleIfStatements");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(BasicRulesTest.class);
    }

}
