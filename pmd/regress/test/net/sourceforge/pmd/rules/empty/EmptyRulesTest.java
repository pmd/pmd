/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.empty;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class EmptyRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("empty", "EmptyCatchBlock");
        addRule("empty", "EmptyFinallyBlock");
        addRule("empty", "EmptyIfStmt");
        addRule("empty", "EmptyInitializer");
        addRule("empty", "EmptyStatementBlock");
        addRule("empty", "EmptyStatementNotInLoop");
        addRule("empty", "EmptyStaticInitializer");
        addRule("empty", "EmptySwitchStatements");
        addRule("empty", "EmptySynchronizedBlock");
        addRule("empty", "EmptyTryBlock");
        addRule("empty", "EmptyWhileStmt");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(EmptyRulesTest.class);
    }
}
