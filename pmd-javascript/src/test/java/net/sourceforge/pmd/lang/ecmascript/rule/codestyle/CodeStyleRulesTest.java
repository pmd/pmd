/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.rule.codestyle;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class CodeStyleRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/ecmascript/codestyle.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "AssignmentInOperand");
        addRule(RULESET, "ForLoopsMustUseBraces");
        addRule(RULESET, "IfElseStmtsMustUseBraces");
        addRule(RULESET, "IfStmtsMustUseBraces");
        addRule(RULESET, "NoElseReturn");
        addRule(RULESET, "UnnecessaryBlock");
        addRule(RULESET, "UnnecessaryParentheses");
        addRule(RULESET, "UnreachableCode");
        addRule(RULESET, "WhileLoopsMustUseBraces");
    }
}
