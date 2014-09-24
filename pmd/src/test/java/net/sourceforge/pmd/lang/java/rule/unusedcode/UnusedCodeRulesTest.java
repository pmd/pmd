/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.unusedcode;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Rule tests for the unused code ruleset.
 */
public class UnusedCodeRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-unusedcode";

    /**
     * Configure the rules.
     */
    @Override
    public void setUp() {
        addRule(RULESET, "UnusedFormalParameter");
        addRule(RULESET, "UnusedLocalVariable");
        addRule(RULESET, "UnusedPrivateField");
        addRule(RULESET, "UnusedPrivateMethod");
        addRule(RULESET, "UnusedModifier");
    }
}
