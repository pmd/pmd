/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.braces;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class BracesRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-braces";

    @Override
    public void setUp() {
        addRule(RULESET, "ForLoopsMustUseBraces");
        addRule(RULESET, "IfElseStmtsMustUseBraces");
        addRule(RULESET, "IfStmtsMustUseBraces");
        addRule(RULESET, "WhileLoopsMustUseBraces");
    }
}
