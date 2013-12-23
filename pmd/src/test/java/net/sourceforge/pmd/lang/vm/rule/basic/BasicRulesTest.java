package net.sourceforge.pmd.lang.vm.rule.basic;

import org.junit.Before;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class BasicRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "vm-basic";

    @Before
    public void setUp() {
        addRule(RULESET, "AvoidDeeplyNestedIfStmts");
        addRule(RULESET, "CollapsibleIfStatements");
        addRule(RULESET, "ExcessiveTemplateLengthRule");
        addRule(RULESET, "AvoidReassigningParameters");
        addRule(RULESET, "EmptyIfStmt");
        addRule(RULESET, "EmptyForeachStmtRule");
        addRule(RULESET, "UnusedMacroParameter");
        addRule(RULESET, "NoInlineJavaScript");
        addRule(RULESET, "NoInlineStyles");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(BasicRulesTest.class);
    }

}
