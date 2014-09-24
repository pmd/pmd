/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.vm.rule.basic;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class BasicRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "vm-basic";

    @Override
    public void setUp() {
        addRule(RULESET, "AvoidDeeplyNestedIfStmts");
        addRule(RULESET, "CollapsibleIfStatements");
        addRule(RULESET, "ExcessiveTemplateLength");
        addRule(RULESET, "AvoidReassigningParameters");
        addRule(RULESET, "EmptyIfStmt");
        addRule(RULESET, "EmptyForeachStmt");
        addRule(RULESET, "UnusedMacroParameter");
        addRule(RULESET, "NoInlineJavaScript");
        addRule(RULESET, "NoInlineStyles");
    }
}
