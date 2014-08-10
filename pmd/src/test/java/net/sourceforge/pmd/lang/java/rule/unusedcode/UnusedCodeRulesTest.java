/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.unusedcode;

import net.sourceforge.pmd.testframework.PMDTestRunner;
import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.runner.RunWith;

/**
 * Rule tests for the unused code ruleset.
 */
@RunWith(PMDTestRunner.class)
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
