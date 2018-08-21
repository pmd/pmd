/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.codestyle;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class CodeStyleRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/apex/codestyle.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "ClassNamingConventions");
        addRule(RULESET, "ForLoopsMustUseBraces");
        addRule(RULESET, "IfElseStmtsMustUseBraces");
        addRule(RULESET, "IfStmtsMustUseBraces");
        addRule(RULESET, "MethodNamingConventions");
        addRule(RULESET, "OneDeclarationPerStatement");
        addRule(RULESET, "VariableNamingConventions");
        addRule(RULESET, "WhileLoopsMustUseBraces");
    }
}
