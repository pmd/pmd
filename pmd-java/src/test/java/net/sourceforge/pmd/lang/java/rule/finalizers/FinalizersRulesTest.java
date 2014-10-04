/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.finalizers;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class FinalizersRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-finalizers";

    @Override
    public void setUp() {
        addRule(RULESET, "AvoidCallingFinalize");
        addRule(RULESET, "EmptyFinalizer");
        addRule(RULESET, "FinalizeDoesNotCallSuperFinalize");
        addRule(RULESET, "FinalizeOnlyCallsSuperFinalize");
        addRule(RULESET, "FinalizeOverloaded");
        addRule(RULESET, "FinalizeShouldBeProtected");
    }
}
