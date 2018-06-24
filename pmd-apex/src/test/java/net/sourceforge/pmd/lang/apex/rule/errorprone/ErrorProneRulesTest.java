/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class ErrorProneRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/apex/errorprone.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "AvoidDirectAccessTriggerMap");
        addRule(RULESET, "AvoidHardcodingId");
        addRule(RULESET, "AvoidNonExistentAnnotations");
        addRule(RULESET, "EmptyCatchBlock");
        addRule(RULESET, "EmptyIfStmt");
        addRule(RULESET, "EmptyStatementBlock");
        addRule(RULESET, "EmptyTryOrFinallyBlock");
        addRule(RULESET, "EmptyWhileStmt");
        addRule(RULESET, "MethodWithSameNameAsEnclosingClass");
    }
}
