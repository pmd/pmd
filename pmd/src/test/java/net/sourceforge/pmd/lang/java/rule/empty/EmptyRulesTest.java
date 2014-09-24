/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.empty;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class EmptyRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-empty";

    @Override
    public void setUp() {
        addRule(RULESET, "EmptyCatchBlock");
        addRule(RULESET, "EmptyFinallyBlock");
        addRule(RULESET, "EmptyIfStmt");
        addRule(RULESET, "EmptyInitializer");
        addRule(RULESET, "EmptyStatementBlock");
        addRule(RULESET, "EmptyStatementNotInLoop");
        addRule(RULESET, "EmptyStaticInitializer");
        addRule(RULESET, "EmptySwitchStatements");
        addRule(RULESET, "EmptySynchronizedBlock");
        addRule(RULESET, "EmptyTryBlock");
        addRule(RULESET, "EmptyWhileStmt");
    }
}
