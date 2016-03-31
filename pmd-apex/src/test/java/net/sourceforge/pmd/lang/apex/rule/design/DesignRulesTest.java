/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.design;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Tests all the rules, that are in the design ruleset.
 */
public class DesignRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "apex-design";

    @Override
    public void setUp() {
        addRule(RULESET, "AvoidDeeplyNestedIfStmts");
    }
}
