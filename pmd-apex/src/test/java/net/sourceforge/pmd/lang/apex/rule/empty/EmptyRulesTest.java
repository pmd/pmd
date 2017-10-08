/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.empty;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class EmptyRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "apex-empty";

    @Override
    public void setUp() {
        addRule(RULESET, "EmptyCatchBlock");
        addRule(RULESET, "EmptyIfStmt");
        addRule(RULESET, "EmptyTryOrFinallyBlock");
        addRule(RULESET, "EmptyWhileStmt");
        addRule(RULESET, "EmptyStatementBlock");
    }
}
